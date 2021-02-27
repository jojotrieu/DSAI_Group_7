package com.example.application.services.silichatbot.knowledge;

import lombok.Data;
import org.jgrapht.graph.DefaultWeightedEdge;

@Data
public class Relation extends DefaultWeightedEdge {
    private String label;
}
