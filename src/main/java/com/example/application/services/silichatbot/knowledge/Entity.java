package com.example.application.services.silichatbot.knowledge;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

@Data
public class Entity {
    private String label;
    @EqualsAndHashCode.Exclude
    private HashMap<String, String> attributes = new HashMap<>();
}
