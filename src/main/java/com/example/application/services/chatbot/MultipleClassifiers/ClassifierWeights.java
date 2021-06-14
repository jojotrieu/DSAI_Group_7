package com.example.application.services.chatbot.MultipleClassifiers;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.silichatbot.Corpus2Vec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.FileLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import org.deeplearning4j.eval.Evaluation;

import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class ClassifierWeights {
    private static final String PATH = "src/main/java/com/example/application/services/chatbot/MultipleClassifiers/data2/";
    private static final String PATH2MOD = "src/main/java/com/example/application/services/chatbot/MultipleClassifiers/Models/";
    private static int batchSize = 32;
    private static  int vectorSize = 300;               //Size of the word vectors. 300 in the Google News model
    private  static int nEpochs = 20;                    //Number of epochs (full passes of training data) to train on
    private  static int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this

    private  static int cnnLayerFeatureMaps = 100;      //Number of feature maps / channels / depth for each CNN layer
    private  static PoolingType globalPoolingType = PoolingType.MAX;
    private static  Random rng = new Random(12345); //For shuffling repeatability

    private static void train(String skill, Word2Vec w2v) throws IOException {



        Nd4j.getMemoryManager().setAutoGcWindow(5000);

        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.RELU)
                .activation(Activation.LEAKYRELU)
                .updater(new Adam(0.01))
                .convolutionMode(ConvolutionMode.Same)      //This is important so we can 'stack' the results later
                .l2(0.0001)
                .graphBuilder()
                .addInputs("input")
                .addLayer("cnn3", new ConvolutionLayer.Builder()
                        .kernelSize(3,vectorSize)
                        .stride(1,vectorSize)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                .addLayer("cnn4", new ConvolutionLayer.Builder()
                        .kernelSize(4,vectorSize)
                        .stride(1,vectorSize)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                .addLayer("cnn5", new ConvolutionLayer.Builder()
                        .kernelSize(5,vectorSize)
                        .stride(1,vectorSize)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                //MergeVertex performs depth concatenation on activations: 3x[minibatch,100,length,300] to 1x[minibatch,300,length,300]
                .addVertex("merge", new MergeVertex(), "cnn3", "cnn4", "cnn5")
                //Global pooling: pool over x/y locations (dimensions 2 and 3): Activations [minibatch,300,length,300] to [minibatch, 300]
                .addLayer("globalPool", new GlobalPoolingLayer.Builder()
                        .poolingType(globalPoolingType)
                        .dropOut(0.5)
                        .build(), "merge")
                .addLayer("out", new OutputLayer.Builder()
                        .lossFunction(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX)
                        .nOut(2)    //2 classes: positive or negative
                        .build(), "globalPool")
                .setOutputs("out")
                //Input has shape [minibatch, channels=1, length=1 to 256, 300]
                .setInputTypes(InputType.convolutional(truncateReviewsToLength, vectorSize, 1))
                .build();

        ComputationGraph net = new ComputationGraph(config);
        net.init();

        //Load word vectors and get the DataSetIterators for training and testing
        System.out.println("Loading word vectors and creating DataSetIterators");
        /**
         * loadStaticModel keeps the model in host memory (not on GPU memory) as read-only mode.
         * We can also use readWord2VecModel method.
         */
        WordVectors wordVectors = w2v;

        DataSetIterator trainIter = getDataSetIterator(true, wordVectors, batchSize, truncateReviewsToLength, skill);
        DataSetIterator testIter = getDataSetIterator(false, wordVectors, batchSize, truncateReviewsToLength, skill);

        System.out.println("Starting training");
        net.setListeners(new ScoreIterationListener(100));

        for (int i = 0; i < nEpochs; i++) {
            net.fit(trainIter);
            //System.out.println("Epoch " + i + " complete. Starting evaluation:");

            //Run evaluation. This is on 25k reviews, so can take some time
            Evaluation evaluation = net.evaluate(testIter);

           // System.out.println(evaluation.stats());
        }

        //After training: load a single sentence and generate a prediction
        String pathFirstNegativeFile = FilenameUtils.concat(PATH, skill +"/test/neg/0.txt");
        String contentsFirstNegative = FileUtils.readFileToString(new File(pathFirstNegativeFile),"UTF-8");
        INDArray featuresFirstNegative = ((CnnSentenceDataSetIterator)testIter).loadSingleSentence(contentsFirstNegative);
//        System.out.println();
//        System.out.println(contentsFirstNegative.substring(0,contentsFirstNegative.indexOf("\n")));
//        System.out.println(featuresFirstNegative.shape());
//        System.out.println(w2v.getWordVectors(new ArrayList<>(List.of(contentsFirstNegative.split(" ")))));
        INDArray predictionsFirstNegative = net.outputSingle(featuresFirstNegative);
//        INDArray test = w2v.getWordVectors(new ArrayList<>(List.of(contentsFirstNegative.split(" "))));
//        INDArray predtest = net.outputSingle(test.reshape(1,1,test.shape()[0], 200));
        List<String> labels = testIter.getLabels();

        System.out.println("\n\nPredictions for first negative review:");
        for( int i=0; i<labels.size(); i++ ){
            System.out.println("P(" + labels.get(i) + ") = " + predictionsFirstNegative.getDouble(i));
        }
//        System.out.println("yoyoyo test");
//        for( int i=0; i<labels.size(); i++ ){
//            System.out.println("P(" + labels.get(i) + ") = " + predtest.getDouble(i));
//        }
        net.save(new File(PATH2MOD+skill));

    }

    private static DataSetIterator getDataSetIterator(boolean isTraining, WordVectors wordVectors, int minibatchSize,
                                                      int maxSentenceLength, String skill ){


        Map<String, List<File>> reviewFilesMap = new HashMap<>();
        reviewFilesMap.put("positive", Arrays.asList(new File(PATH+skill+(isTraining?"/train":"/test")+"/pos").listFiles()));
        reviewFilesMap.put("negative", Arrays.asList(new File(PATH+skill+(isTraining?"/train":"/test")+"/neg").listFiles()));
        LabeledSentenceProvider sentenceProvider = new FileLabeledSentenceProvider(reviewFilesMap,rng);
        CnnSentenceDataSetIterator iterator = new CnnSentenceDataSetIterator.Builder(CnnSentenceDataSetIterator.Format.CNN2D)
                .sentenceProvider(sentenceProvider)
                .wordVectors(wordVectors)
                .minibatchSize(minibatchSize)
                .maxSentenceLength(maxSentenceLength)
                .useNormalizedWordVectors(false)
                .build();
        return iterator;
    }

    public static void main(String[] args){
        ChatBot.init();
        Corpus2Vec corpus2Vec = new Corpus2Vec("corpus.txt");
//        corpus2Vec.cleanCorpus();
//        corpus2Vec.init();
        Word2Vec w2v = corpus2Vec.getWord2Vec();

        try {

            for (String skill : CFG.getAllActionRules()) {
                System.out.println("TRAINING FOR SKILL: "+skill);
                train(skill.substring(1,skill.length()-1), w2v);
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}
