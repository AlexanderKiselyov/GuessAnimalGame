package org.example;

public class Leaf extends Node {
    private final String animal;

    public Leaf(String animal, Node parent, Boolean isYesAnswer) {
        super(parent, isYesAnswer);
        this.animal = animal;
    }

    public String getAnimal() {
        return animal;
    }
}