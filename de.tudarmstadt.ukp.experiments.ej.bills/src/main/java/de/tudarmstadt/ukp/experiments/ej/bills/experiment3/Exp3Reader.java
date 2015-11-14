/**
 * Copyright 2015
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package de.tudarmstadt.ukp.experiments.ej.bills.experiment3;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.io.TCReaderSingleLabel;
import de.tudarmstadt.ukp.dkpro.tc.api.type.TextClassificationOutcome;
import de.tudarmstadt.ukp.dkpro.tc.core.io.PairReader_ImplBase;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Action;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Bill;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Action.ActionType;
import de.tudarmstadt.ukp.experiments.ej.bills.io.MyJsonReader;


/**
 * A DKPro TC reader, which reads Bill for Experiment 3.
 */
public class Exp3Reader
    extends PairReader_ImplBase
    implements TCReaderSingleLabel

{
    /**
     * Character encoding of the input data
     */
    public static final String PARAM_ENCODING = ComponentParameters.PARAM_SOURCE_ENCODING;
    @ConfigurationParameter(name = PARAM_ENCODING, mandatory = true, defaultValue = "UTF-8")
    private String encoding;

    /**
     * Language of the input data
     */
    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true)
    private String language;

    /**
     * Path to the file containing the Bills
     */
    public static final String PARAM_JSON_FILE = "JsonFile";
    @ConfigurationParameter(name = PARAM_JSON_FILE, mandatory = true)
    private String jsonFile;

    private Bill bill;
    private LineIterator jsonIterator;
    
    private int jsonSize; //total number of bills to be read

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        
        // open JSON file here
        try {
            jsonSize = FileUtils.readLines(new File(jsonFile)).size();
            jsonIterator = FileUtils.lineIterator(new File(jsonFile), "UTF-8");
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public boolean hasNext()
        throws IOException, CollectionException
    {
        return jsonIterator.hasNext();
    }
    
    @Override
    public String getCollectionId1() {
        return jsonFile.replace(".json", "") + "Titles";
    }
    
    @Override
    public String getCollectionId2() {
        return jsonFile.replace(".json", "") + "Descriptions";
    }
    
    @Override
    public String getDocumentId1() {
        return "Bill" + bill.getId() + "Title";
    }
    
    @Override
    public String getDocumentId2() {
        return "Bill" + bill.getId() + "Description";
    }

    @Override
    public String getTitle1() {
        return "Bill" + bill.getId() + "Title";
    }
    
    @Override
    public String getTitle2() {
        return "Bill" + bill.getId() + "Description";
    }

    @Override
    public String getLanguage1() {
        return language;
    }
    
    @Override
    public String getLanguage2() {
        return language;
    }

    @Override
    public String getText1() 
            throws TextClassificationException
    {
        return bill.getTitle();
    }
    
    @Override
    public String getText2()
            throws TextClassificationException
    {
        return bill.getDescription();
    }
    
    @Override
    public void getNext(JCas aJCas)
        throws IOException, CollectionException
    {
        // reading the json file line into a Bill object
        try {
            bill = MyJsonReader.parseBill(jsonIterator.nextLine());
        }
        catch (ParseException e) {
            throw new IOException(e);
        }
        super.getNext(aJCas);
        
        // setting the outcome / label for this document
        TextClassificationOutcome outcome = new TextClassificationOutcome(aJCas);
        outcome.setOutcome(getTextClassificationOutcome(aJCas));
        outcome.addToIndexes();
    }

    @Override
    public String getTextClassificationOutcome(JCas jcas)
        throws CollectionException
    {
        List<Action> actions = bill.getActions();
        Date enacted = new Date();
        for(Action action: actions){
            if(action.getActionType().equals(ActionType.ENACTED)){
                enacted = action.getDate();
            }
        }
        String enactedOrFailed = "failed";
        if(enacted != null){
            enactedOrFailed = "enacted";
        }
        return enactedOrFailed;
    }

    @Override
    public Progress[] getProgress()
    {
        return new Progress[] { new ProgressImpl(bill.getId(), jsonSize, "bills") };
    }
}