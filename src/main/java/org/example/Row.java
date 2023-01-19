package org.example;

public class Row {
    private final String question;
    private final String animal;

    public Row(String question, String animal) {
        this.question = question;
        this.animal = animal;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnimal() {
        return animal;
    }
}
