package com.example.application.models;

import io.swagger.models.auth.In;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;
import java.util.stream.Collectors;

public class Word2Graph {
    //VARIABLES
    private Graph<String,DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    private Map<String,Double> trace = new HashMap<>();
    private PriorityQueue<Pair<String,Integer>> domains;
    private boolean covered = false;
    //CONSTANTS
    private final int NUM_OF_DOMAINS = 100;
    private final double RATE_OF_DECAY=2;
    private final int WORKING_MEMORY = 300;

    public void buildGraph(){

    }

    public double[] getWordVector(String word, double budget){
        double [] wordVector = new double[NUM_OF_DOMAINS];
        if(graph.containsVertex(word)){
            List<String> neighbors = Graphs.neighborListOf(graph,word);
            List<String> domainNeighbors = new ArrayList<>();
            for(String n : neighbors){
                domains.forEach(d -> {
                    if(d.getFirst().equals(n)){
                        domainNeighbors.add(d.getFirst());
                    }
                });
            }
            double totalWeight = 0;
            for(String d : domainNeighbors){
                totalWeight += graph.getEdgeWeight(graph.getEdge(d,word));
            }
            for(String d : domainNeighbors){
                trace.put(d,trace.get(d) + (budget/totalWeight));
            }
            List<Double> traceValues = new ArrayList<>(trace.values());
            for (int i = 0; i < traceValues.size(); i++) {
                wordVector[i]=traceValues.get(i);
            }
        }
        return wordVector;
    }

    private void decay(){
        for(Map.Entry<String,Double> e : trace.entrySet()){
            e.setValue(e.getValue()/RATE_OF_DECAY);
        }
    }

    private void findDomains (){
        domains = new PriorityQueue<>(Comparator.comparingInt(p -> -p.getSecond()));
        PriorityQueue<Pair<String,Integer>> domainInverse =
                new PriorityQueue<>(Comparator.comparingInt(Pair::getSecond));
        for(String vertex : graph.vertexSet()){
            Pair<String,Integer> word = new Pair<>(vertex,graph.degreeOf(vertex));
            domains.add(word);
            domainInverse.add(word);
            if(domains.size()==NUM_OF_DOMAINS){
                domains.remove(domainInverse.poll());
            }
        }
        vertexCover();
        if(covered){
            domains.forEach(d -> trace.put(d.getFirst(),0d));
        }
    }

    private void vertexCover(){
        Set<DefaultWeightedEdge> edges = graph.edgeSet();
        Set<DefaultWeightedEdge> cover = new HashSet<>();
        domains.forEach(word -> cover.addAll(graph.edgesOf(word.getFirst())));
        covered = edges.size()==cover.size();
    }

    private void addSentence(String... sentence){
        //go through every single word in sentence
        for(String word : sentence){
            //if graph already contains the word
            if(graph.containsVertex(word)){
                for(String neighbor : sentence){
                    //for all neighbors (not the word itself)
                    if(!neighbor.equals(word)){
                        addEdgeWeight(word,neighbor);
                    }
                }
            } else {
                //if graph doesn't have word yet, create a corresponding vertex
                graph.addVertex(word);
                for(String neighbor : sentence){
                    if(!neighbor.equals(word)){
                        //for all neighbors (not the word itself) create edge, 1 is default starting weight
                        graph.addEdge(word,neighbor);
                    }
                }
            }
        }
    }

    private void addEdgeWeight(String word, String neighbor){
        if(graph.containsEdge(word,neighbor)){
            //add +1 to edge if it exists already
            DefaultWeightedEdge edge = graph.getEdge(word,neighbor);
            graph.setEdgeWeight(edge,graph.getEdgeWeight(edge)+1);
        } else {
            //create edge if it doesn't exist already, 1 is default starting weight
            graph.addEdge(word,neighbor);
        }
    }

    private void addWindow(String word, String... neighbors){
        if(graph.containsVertex(word)){
            for(String neighbor : neighbors){
                addEdgeWeight(word,neighbor);
            }
        }
    }
}
