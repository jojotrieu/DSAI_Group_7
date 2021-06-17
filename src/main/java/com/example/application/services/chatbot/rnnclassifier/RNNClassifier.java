package com.example.application.services.chatbot.rnnclassifier;

import com.example.application.services.chatbot.spellcheckML.Rephraser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RNNClassifier {

    private static MultiLayerNetwork model;

    public static void init() throws IOException, InterruptedException {
        String variationsPath = "src/main/java/com/example/application/services/chatbot/spellcheckML/phraseVariations.csv";
        File dataPath = new File("src/main/java/com/example/application/services/chatbot/rnnclassifier/data/");
        if(!dataPath.exists()){
            InputStream initialStream = new FileInputStream(variationsPath);
            String variations = IOUtils.toString(initialStream, Charset.defaultCharset());

            String [] lines = variations.split("\n");

            int index = 0;
            ArrayList<String> linesList = new ArrayList<>();

            for (String line : lines) {
                String newLine = "";
                newLine = line.replaceAll("\\s+", ", ");
                linesList.add(newLine);
            }

            for (String line : linesList) {
                File outPath = new File(dataPath, index + ".csv");
                FileUtils.writeStringToFile(outPath, line, Charset.defaultCharset());
                index += 1;
            }
        } else {
            System.out.println("files already exist");
        }

        int batchSize = 10000;
        int numLabelClasses = 52;
        CSVSequenceRecordReader trainRR = new CSVSequenceRecordReader(0, ", ");
        trainRR.initialize(new NumberedFileInputSplit( dataPath.getAbsolutePath() + "/%d.csv", 0, 50000));
        SequenceRecordReaderDataSetIterator trainIter =
                new SequenceRecordReaderDataSetIterator(trainRR, batchSize, numLabelClasses, 90);
        CSVSequenceRecordReader testRR = new CSVSequenceRecordReader(0, ", ");
        testRR.initialize(new NumberedFileInputSplit( dataPath.getAbsolutePath() + "/%d.csv", 50001, 65001));
        SequenceRecordReaderDataSetIterator testIter =
                new SequenceRecordReaderDataSetIterator(testRR, batchSize, numLabelClasses, 90);


        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)    //Random number generator seed for improved repeatability. Optional.
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(0.005))
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)  //Not always required, but helps with this data set
                .gradientNormalizationThreshold(0.5)
                .list()
                .layer(0, new LSTM.Builder().activation(Activation.TANH).nIn(90).nOut(10).build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX).nIn(10).nOut(numLabelClasses).build())
                .build();

        model = new MultiLayerNetwork(conf);
        model.setListeners(new ScoreIterationListener(20));

        int numEpochs = 1;
        model.fit(trainIter, numEpochs);

        Evaluation evaluation = model.evaluate(testIter);
        System.out.println("Accuracy: "+evaluation.accuracy());
        System.out.println("Precision: "+evaluation.precision());
        System.out.println("Recall: "+evaluation.recall());

    }

    public static int[] predict(String phrase){
        return model.predict(Rephraser.convert(phrase));
    }

}
