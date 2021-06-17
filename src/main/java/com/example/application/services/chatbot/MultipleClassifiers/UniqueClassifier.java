package com.example.application.services.chatbot.MultipleClassifiers;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.silichatbot.Corpus2Vec;
import lombok.EqualsAndHashCode;
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

public class UniqueClassifier {
    private static final String PATH2DATA = "src/main/java/com/example/application/services/chatbot/MultipleClassifiers/data/";
    private static final String PATH = "src/main/java/com/example/application/services/chatbot/MultipleClassifiers/";
    private static int batchSize = 32;
    private static  int vectorSize = 200;               //Size of the word vectors. 300 in the Google News model
    private  static int nEpochs = 10;                    //Number of epochs (full passes of training data) to train on
    private  static int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this

    private  static int cnnLayerFeatureMaps = 100;      //Number of feature maps / channels / depth for each CNN layer
    private  static PoolingType globalPoolingType = PoolingType.MAX;
    private static  Random rng = new Random(12345); //For shuffling repeatability
    private static Word2Vec w2v;
    private static ComputationGraph model;

    @EqualsAndHashCode.Exclude
    private static Map<String, Integer> skillToInd = null;
    @EqualsAndHashCode.Exclude
    private static String[] indToSkill = null;

    private static void init(){
        List<String>a= CFG.getAllActionRules();
        Collections.sort(a);
        indToSkill = new String[a.size()];
        skillToInd = new HashMap<>();
        int i=0;
        for (String s : a) {
            indToSkill[i]=s;
            skillToInd.put(s, i++);
        }

        Corpus2Vec corpus2Vec = new Corpus2Vec("corpus.txt");
        w2v = corpus2Vec.getWord2Vec();
        try {
            model = ComputationGraph.load(new File(PATH + "UniqueModel"), true);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void train() throws IOException {

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
                        .nOut(52)    //2 classes: positive or negative
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

        DataSetIterator trainIter = getDataSetIterator(true, wordVectors, batchSize, truncateReviewsToLength);
        DataSetIterator testIter = getDataSetIterator(false, wordVectors, batchSize, truncateReviewsToLength);

        System.out.println("Starting training");
        net.setListeners(new ScoreIterationListener(100));

        for (int i = 0; i < nEpochs; i++) {
            net.fit(trainIter);
            //System.out.println("Epoch " + i + " complete. Starting evaluation:");

            //Run evaluation. This is on 25k reviews, so can take some time
            Evaluation evaluation = net.evaluate(testIter);

            System.out.println(evaluation.stats());
        }

        //After training: load a single sentence and generate a prediction
        String skill = indToSkill[rng.nextInt(indToSkill.length)];
        String pathToSkill = FilenameUtils.concat(PATH2DATA, skill.substring(1,skill.length()-1) +"/test/0.txt");
        String contentsFirstNegative = FileUtils.readFileToString(new File(pathToSkill),"UTF-8");
        INDArray featuresFirstNegative = ((CnnSentenceDataSetIterator)testIter).loadSingleSentence(contentsFirstNegative);
        INDArray predictionsFirstNegative = net.outputSingle(featuresFirstNegative);
        List<String> labels = testIter.getLabels();

        System.out.println("\n\nPredictions for random skill:" + skill);
        for( int i=0; i<labels.size(); i++ ){
            System.out.println("P(" + labels.get(i) + ") = " + predictionsFirstNegative.getDouble(i));
        }
        net.save(new File(PATH+"UniqueModel"));

    }

    private static DataSetIterator getDataSetIterator(boolean isTraining, WordVectors wordVectors, int minibatchSize,
                                                      int maxSentenceLength ){

        Map<String, List<File>> reviewFilesMap = new HashMap<>();
        if(isTraining) {
            for (String s : indToSkill) {
                reviewFilesMap.put(s, Arrays.asList(Objects.requireNonNull(new File(PATH2DATA + s.substring(1, s.length() - 1) + "/train").listFiles())));
            }
        }else{
            for (String s : indToSkill) {
                ArrayList<File> files = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(PATH2DATA + s.substring(1, s.length() - 1) + "/unknown").listFiles())));
//                files.addAll(Arrays.asList(Objects.requireNonNull(new File(PATH2DATA + s.substring(1, s.length() - 1) + "/test").listFiles())));
                reviewFilesMap.put(s, files);
//                reviewFilesMap.put(s, Arrays.asList(Objects.requireNonNull(new File(PATH2DATA + s.substring(1, s.length() - 1) + "/unknown").listFiles())));
//                for(File f : Objects.requireNonNull(new File(PATH2DATA + s.substring(1, s.length() - 1) + "/unknown").listFiles())){
//                    reviewFilesMap.get(s).add(f);
//                }

            }
        }
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

    public static String predict(String query){
        INDArray value = w2v.getWordVectors(new ArrayList<>(List.of(query.split(" "))));

        if(value.shape().length>0){
            INDArray output = model.outputSingle(w2v.getWordVectors(List.of(query.split(" "))).reshape(1,1,value.shape()[0], value.shape()[1]));
//            System.out.println(output);
//            System.out.println(Arrays.toString(output.shape()));

            double max =0.0;
            int ind=0;
            for (int i = 0; i < output.shape()[1]; i++) {
                if(output.getDouble(i)>max) {
                    max = output.getDouble(i);
                    ind = i;
                }
            }
            return indToSkill[ind];
        }
        return "Undefined";
    }

    public static Map<String, Double> predictWith(String query){
        INDArray value = w2v.getWordVectors(new ArrayList<>(List.of(query.split(" "))));
        Map<String, Double> result = new HashMap<>();
        if(value.shape().length>0){
            INDArray output = model.outputSingle(w2v.getWordVectors(List.of(query.split(" "))).reshape(1,1,value.shape()[0], value.shape()[1]));
//            System.out.println(output);
//            System.out.println(Arrays.toString(output.shape()));

            double max =0.0;
            int ind=0;
            for (int i = 0; i < output.shape()[1]; i++) {
                if(output.getDouble(i)>max) {
                    max = output.getDouble(i);
                    ind = i;
                }
            }

            result.put(indToSkill[ind], max);
        }
        return result;
    }


    public static void main(String[] args){
        ChatBot.init();
//        Corpus2Vec corpus2Vec = new Corpus2Vec("corpus.txt");
//        init();
//        corpus2Vec.cleanCorpus();
//        corpus2Vec.init();
//        Word2Vec w2v = corpus2Vec.getWord2Vec();

        // write the model 5 epochs
    /*    try {
            train();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

     */


        init();
        // test the model
   /*     try {
            String pathtest = PATH2DATA;
            int correct = 0, wrong = 0;
            for (String skill : CFG.getAllActionRules()) {
                String filePath = pathtest + skill.substring(1, skill.length() - 1) + "/test/";
                List<File> test = Arrays.asList(new File(filePath).listFiles());
                for(File t : test) {
                    String query = FileUtils.readFileToString(t);
                    String p = predict(query);
                    if (p.equals(skill)) correct += 1;
                    else wrong += 1;
                }
            }
            System.out.println("Correct: "+correct+"\nWrong: "+wrong);
            System.out.println();
        }catch (IOException e){
            e.printStackTrace();
        }

    */

        DataSetIterator iterator = getDataSetIterator(false, w2v, batchSize,truncateReviewsToLength);
        Evaluation eval = model.evaluate(iterator);
        System.out.println(eval.stats(false, true));
    }
}