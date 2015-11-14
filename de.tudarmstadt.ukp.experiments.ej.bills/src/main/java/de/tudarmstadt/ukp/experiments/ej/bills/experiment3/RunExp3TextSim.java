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

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import weka.classifiers.functions.SMO;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.task.BatchTask.ExecutionPolicy;
import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import de.tudarmstadt.ukp.dkpro.tc.core.Constants;
import de.tudarmstadt.ukp.dkpro.tc.examples.util.DemoUtils;
import de.tudarmstadt.ukp.dkpro.tc.features.pair.similarity.CosineFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.features.pair.similarity.GreedyStringTilingFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.ml.ExperimentCrossValidation;
import de.tudarmstadt.ukp.dkpro.tc.ml.report.BatchCrossValidationReport;
import de.tudarmstadt.ukp.dkpro.tc.weka.WekaClassificationAdapter;
import de.tudarmstadt.ukp.experiments.ej.bills.io.CombineTestResultsClassification;
import dkpro.similarity.algorithms.lexical.string.CosineSimilarity;

/**
 * This demo uses the {@link SimpleDkproTCReader}.
 */

public class RunExp3TextSim
    implements Constants
{
    public static final String LANGUAGE_CODE = "en";
    public static final int NUM_FOLDS = 2;//only 2 to save runtime
    public static String filepathToData;
    public static final String EXPERIMENTNAME = "Exp3TextSim";
    
    public static void main(String[] args)
        throws Exception
    {
    	// This is used to ensure that the required DKPRO_HOME environment variable is set.
    	// Ensures that people can run the experiments even if they haven't read the setup instructions first :)
    	// Don't use this in real experiments! Read the documentation and set DKPRO_HOME as explained there.
    	DemoUtils.setDkproHome(Exp3Reader.class.getSimpleName());
    	
    	filepathToData = 
                DkproContext.getContext().getWorkspace("FiscalNote").getAbsolutePath() + "/fn_data/data.json";
    	
        RunExp3TextSim demo = new RunExp3TextSim();
        demo.runCrossValidation(getParameterSpace());
        CombineTestResultsClassification.callFromGroovyStarter(EXPERIMENTNAME);
    }

    // ##### CV #####
    protected void runCrossValidation(ParameterSpace pSpace)
        throws Exception
    {
        ExperimentCrossValidation batch = new ExperimentCrossValidation(
                EXPERIMENTNAME, WekaClassificationAdapter.class, NUM_FOLDS);
        batch.setPreprocessing(getPreprocessing());
        batch.setParameterSpace(pSpace);
        batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
        batch.addReport(BatchCrossValidationReport.class);

        // Run
        Lab.getInstance().run(batch);
    }

    public static ParameterSpace getParameterSpace()
    {
        Map<String, Object> dimReaders = new HashMap<String, Object>();
        dimReaders.put(DIM_READER_TRAIN, Exp3Reader.class);
        dimReaders.put(
                DIM_READER_TRAIN_PARAMS,
                Arrays.asList(new Object[] {
                        Exp3Reader.PARAM_LANGUAGE, LANGUAGE_CODE,
                        Exp3Reader.PARAM_JSON_FILE, filepathToData
                        }));

        @SuppressWarnings("unchecked")
        Dimension<List<String>> dimClassificationArgs = Dimension.create(
                DIM_CLASSIFICATION_ARGS,
                Arrays.asList(new String[] { 
                        SMO.class.getName() 
                        }));

        @SuppressWarnings("unchecked")
        Dimension<List<String>> dimFeatureSets = Dimension.create(
                DIM_FEATURE_SET,
                asList(new String[] { 
                        CosineFeatureExtractor.class.getName(),
                        GreedyStringTilingFeatureExtractor.class.getName()
                        }));

        // parameters to configure feature extractors
        @SuppressWarnings("unchecked")
        Dimension<List<Object>> dimPipelineParameters = Dimension
                .create(DIM_PIPELINE_PARAMS,
                        asList(new Object[] {
                              CosineFeatureExtractor.PARAM_WEIGHTING_MODE_TF, CosineSimilarity.WeightingModeTf.FREQUENCY_LOGPLUSONE ,
                              CosineFeatureExtractor.PARAM_WEIGHTING_MODE_IDF, CosineSimilarity.WeightingModeIdf.LOGPLUSONE ,
                              CosineFeatureExtractor.PARAM_NORMALIZATION_MODE, CosineSimilarity.NormalizationMode.L2
                        }));

        ParameterSpace pSpace = new ParameterSpace(Dimension.createBundle(
                "readers", dimReaders), Dimension.create(
                DIM_LEARNING_MODE, LM_SINGLE_LABEL), Dimension.create(
                DIM_FEATURE_MODE, FM_PAIR), dimPipelineParameters,
                dimFeatureSets, dimClassificationArgs);

        return pSpace;
    }

    // Before we extract ML features, we need to tokenize and segment 
    // words and sentences in the text.
    protected AnalysisEngineDescription getPreprocessing()
        throws ResourceInitializationException
    {
        return createEngineDescription(createEngineDescription(
                BreakIteratorSegmenter.class,
                BreakIteratorSegmenter.PARAM_LANGUAGE, LANGUAGE_CODE));
    }
}
