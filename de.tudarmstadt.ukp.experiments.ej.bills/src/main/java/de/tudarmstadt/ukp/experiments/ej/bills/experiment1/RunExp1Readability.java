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
package de.tudarmstadt.ukp.experiments.ej.bills.experiment1;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import weka.classifiers.functions.SMOreg;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpChunker;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.task.BatchTask.ExecutionPolicy;
import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import de.tudarmstadt.ukp.dkpro.tc.core.Constants;
import de.tudarmstadt.ukp.dkpro.tc.examples.util.DemoUtils;
import de.tudarmstadt.ukp.dkpro.tc.features.readability.AvgLengthExtractor;
import de.tudarmstadt.ukp.dkpro.tc.features.readability.LexicalVariationExtractor;
import de.tudarmstadt.ukp.dkpro.tc.features.readability.ParsePatternExtractor;
import de.tudarmstadt.ukp.dkpro.tc.features.readability.PhrasePatternExtractor;
import de.tudarmstadt.ukp.dkpro.tc.features.readability.TraditionalReadabilityMeasuresFeatureExtractor;
import de.tudarmstadt.ukp.dkpro.tc.features.readability.TypeTokenRatioExtractor;
import de.tudarmstadt.ukp.dkpro.tc.ml.ExperimentCrossValidation;
import de.tudarmstadt.ukp.dkpro.tc.ml.report.BatchCrossValidationReport;
import de.tudarmstadt.ukp.dkpro.tc.weka.WekaRegressionAdapter;
import de.tudarmstadt.ukp.experiments.ej.bills.io.CombineTestResultsRegression;

/**
 * This demo uses the {@link SimpleDkproTCReader}.
 */

public class RunExp1Readability
    implements Constants
{
    public static final String LANGUAGE_CODE = "en";
    public static final int NUM_FOLDS = 2;
    public static String filepathToData;
    public static final String EXPERIMENTNAME = "Exp1Readability";
    
    public static void main(String[] args)
        throws Exception
    {
    	// This is used to ensure that the required DKPRO_HOME environment variable is set.
    	// Ensures that people can run the experiments even if they haven't read the setup instructions first :)
    	// Don't use this in real experiments! Read the documentation and set DKPRO_HOME as explained there.
    	DemoUtils.setDkproHome(Exp1Reader.class.getSimpleName());
    	
    	filepathToData = 
                DkproContext.getContext().getWorkspace("FiscalNote").getAbsolutePath() + "/fn_data/data.json";
    	
//        RunExp1Readability demo = new RunExp1Readability();
//        demo.runCrossValidation(getParameterSpace());
        CombineTestResultsRegression.callFromGroovyStarter(EXPERIMENTNAME);

        Date enddate = new Date();
        System.out.println("Finished: " + enddate.toString());
    }

    // ##### CV #####
    protected void runCrossValidation(ParameterSpace pSpace)
        throws Exception
    {
        ExperimentCrossValidation batch = new ExperimentCrossValidation(
                EXPERIMENTNAME, WekaRegressionAdapter.class, NUM_FOLDS);
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
        dimReaders.put(DIM_READER_TRAIN, Exp1Reader.class);
        dimReaders.put(
                DIM_READER_TRAIN_PARAMS,
                Arrays.asList(new Object[] {
                        Exp1Reader.PARAM_LANGUAGE, LANGUAGE_CODE,
                        Exp1Reader.PARAM_JSON_FILE, filepathToData
                        }));

        @SuppressWarnings("unchecked")
        Dimension<List<String>> dimClassificationArgs = Dimension.create(
                DIM_CLASSIFICATION_ARGS,
                Arrays.asList(new String[] { 
                        SMOreg.class.getName() 
                        }));

        @SuppressWarnings("unchecked")
        Dimension<List<String>> dimFeatureSets = Dimension.create(
                DIM_FEATURE_SET,
                asList(new String[] { 
                        // Here is our list of Readability feature extractors
                        AvgLengthExtractor.class.getName(),
                        LexicalVariationExtractor.class.getName(),
//                        ParsePatternExtractor.class.getName(),//text must be parsed
                        PhrasePatternExtractor.class.getName(), //text must be chunked
                        TraditionalReadabilityMeasuresFeatureExtractor.class.getName(),
                        TypeTokenRatioExtractor.class.getName()
                        }));

        // parameters to configure feature extractors
        @SuppressWarnings("unchecked")
        Dimension<List<Object>> dimPipelineParameters = Dimension
                .create(DIM_PIPELINE_PARAMS,
                        asList(new Object[] {
                                TraditionalReadabilityMeasuresFeatureExtractor.PARAM_ADD_ARI, true, 
                                TraditionalReadabilityMeasuresFeatureExtractor.PARAM_ADD_COLEMANLIAU, true,
                                TraditionalReadabilityMeasuresFeatureExtractor.PARAM_ADD_FLESH, true,
                                TraditionalReadabilityMeasuresFeatureExtractor.PARAM_ADD_FOG, true,
                                TraditionalReadabilityMeasuresFeatureExtractor.PARAM_ADD_KINCAID, true,
                                TraditionalReadabilityMeasuresFeatureExtractor.PARAM_ADD_LIX, true,
                                TraditionalReadabilityMeasuresFeatureExtractor.PARAM_ADD_SMOG, true, 
                                }));


        ParameterSpace pSpace = new ParameterSpace(
                Dimension.createBundle("readers", dimReaders), 
                Dimension.create(DIM_LEARNING_MODE, LM_REGRESSION), 
                Dimension.create(DIM_FEATURE_MODE, FM_DOCUMENT), 
                dimPipelineParameters,
                dimFeatureSets, 
                dimClassificationArgs
                );

        return pSpace;
    }

    // Before we extract ML features, we need to preprocess the text
    protected AnalysisEngineDescription getPreprocessing()
        throws ResourceInitializationException
    {
        return createEngineDescription(
                // The non-Stanford pipeline; Results are lower but 
                // POM can have DKPro Similarity dependencies.
                createEngineDescription(
                BreakIteratorSegmenter.class,
                BreakIteratorSegmenter.PARAM_LANGUAGE, LANGUAGE_CODE),
                createEngineDescription(OpenNlpPosTagger.class, 
                                        OpenNlpPosTagger.PARAM_LANGUAGE,
                                        LANGUAGE_CODE),
                createEngineDescription(StanfordLemmatizer.class),
                createEngineDescription(OpenNlpChunker.class),
                createEngineDescription(OpenNlpParser.class)//,

                // The Stanford pipeline: Stanford Segmenter throws error 
                // if POM includes DKPro Similarity dependencies
//                createEngineDescription(StanfordSegmenter.class), 
//                createEngineDescription(StanfordLemmatizer.class),
//                createEngineDescription(SnowballStemmer.class),
//                createEngineDescription(StanfordPosTagger.class),
//                createEngineDescription(StanfordParser.class),
                );
    }
}
