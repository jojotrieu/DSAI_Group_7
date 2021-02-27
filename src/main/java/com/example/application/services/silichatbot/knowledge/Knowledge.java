package com.example.application.services.silichatbot.knowledge;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class Knowledge {
    private final Graph<Entity,Relation> graph = new SimpleWeightedGraph<>(Relation.class);
    private final Set<Entity> entitySet = graph.vertexSet();
    private final Set<Relation> relationSet = graph.edgeSet();
    private final String ENTITIES_FILE_PATH =
            "src/main/java/com/example/application/services/silichatbot/knowledge/entities.txt";
    private final String RELATIONS_FILE_PATH =
            "src/main/java/com/example/application/services/silichatbot/knowledge/relations.txt";

    public void construct(){
        readEntities();
        readRelations();
    }

    private void readEntities(){
        try {
            File textFile =
                    new File(ENTITIES_FILE_PATH);
            Scanner myReader = new Scanner(textFile);
            // ---START OF READING ENTITIES
            while (myReader.hasNext()) {
                String line = myReader.nextLine();
                Entity entity = new Entity();
                //start label
                StringBuilder label = new StringBuilder();
                while(!line.equals("++++++++++++++++++++++++++++++++++++++++++++++++++++")){
                    label.append(line).append(" ");
                    line = myReader.nextLine();
                }
                entity.setLabel(label.substring(0,label.length()-1));
                //end label
                //start fields and values
                line = myReader.nextLine();
                while(!line.equals("====================================================")){
                    StringBuilder field = new StringBuilder();
                    while(!line.equals("----------------------------------------------------")){
                        field.append(line).append(" ");
                        line = myReader.nextLine();
                    }
                    line = myReader.nextLine();
                    StringBuilder value = new StringBuilder();
                    while(!line.equals("++++++++++++++++++++++++++++++++++++++++++++++++++++")){
                        value.append(line).append(" ");
                        line = myReader.nextLine();
                    }
                    entity.getAttributes()
                            .put(field.substring(0,field.length()-1).toLowerCase()
                                    ,value.substring(0,value.length()-1));
                    line = myReader.nextLine();
                }
                //end fields and values
                graph.addVertex(entity);
            }
            //---END OF READING ENTITIES
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void readRelations(){
        try {
            File textFile =
                    new File(RELATIONS_FILE_PATH);
            Scanner myReader = new Scanner(textFile);

            //---START OF READING RELATIONS
            while(myReader.hasNextLine()){
                String line = myReader.nextLine();
                Relation relation = new Relation();
                String [] arr = line.split(",");
                relation.setLabel(arr[0]);
                Entity entity1 = entitySet.stream()
                        .filter(e -> e.getLabel().equals(arr[1])).findFirst().orElseThrow();
                Entity entity2 = entitySet.stream()
                        .filter(e -> e.getLabel().equals(arr[2])).findFirst().orElseThrow();
                graph.addEdge(entity1,entity2,relation);
            }
            //---END OF READING RELATIONS
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void addEntity(Entity entity){
        String label = entity.getLabel();
        HashMap<String,String> attributes = entity.getAttributes();
        try {
            FileWriter fw = new FileWriter(ENTITIES_FILE_PATH, true);
            fw.write(label);
            fw.write("++++++++++++++++++++++++++++++++++++++++++++++++++++");
            for(Map.Entry<String,String> entry: attributes.entrySet()){
                fw.write(entry.getKey());
                fw.write("----------------------------------------------------");
                fw.write(entry.getValue());
                fw.write("++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }
            fw.write("====================================================");
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void addRelation(String... relation){
        for (int i = 0; i < relation.length-1; i++) {
            relation[i] = relation[i] + ",";
        }
        try {
            FileWriter fw = new FileWriter(RELATIONS_FILE_PATH, true);
            for(String s : relation){
                fw.write(s);
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public Entity getEntity(String label){
        return entitySet.stream()
                .filter(e -> e.getLabel().equals(label)).findFirst().orElseThrow();
    }

    public Relation getRelation(String label1, String label2){
        return graph.getEdge(getEntity(label1),getEntity(label2));
    }

    public Graph<Entity, Relation> getGraph() {
        return graph;
    }

    public Set<Entity> getEntitySet() {
        return entitySet;
    }

    public Set<Relation> getRelationSet() {
        return relationSet;
    }
}
