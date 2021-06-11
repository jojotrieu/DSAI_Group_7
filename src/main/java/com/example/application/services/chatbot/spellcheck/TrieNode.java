package com.example.application.services.chatbot.spellcheck;

public class TrieNode {

    TrieNode[] nodes = new TrieNode[26];
    int count;
    boolean isEnd;

    public int getValue() {
        return count;
    }

    public void incrementValue() {
        count++;
    }

    public TrieNode[] getChildren() {
        return nodes;
    }
}
