package org.example;

public class Node {
    private final String question;
    private Node yesAnswer;
    private Node noAnswer;
    private Node parent;
    private Boolean isYesAnswer;

    public Node(String question, Node parent, Boolean isYesAnswer) {
        this.question = question;
        this.parent = parent;
        this.isYesAnswer = isYesAnswer;
    }

    public Node(Node parent, Boolean isYesAnswer) {
        question = null;
        this.parent = parent;
        this.isYesAnswer = isYesAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public Node getYesAnswer() {
        return yesAnswer;
    }

    public Node getNoAnswer() {
        return noAnswer;
    }

    public Node getParent() {
        return parent;
    }

    public Boolean getIsYesAnswer() {
        return isYesAnswer;
    }

    public void setYesAnswer(Node node) {
        this.yesAnswer = node;
    }

    public void setNoAnswer(Node node) {
        this.noAnswer = node;
    }

    public void changeYesAnswer(Node node, Leaf animal) {
        Node prevAnimal = yesAnswer;
        yesAnswer = node;
        node.yesAnswer = animal;
        node.noAnswer = prevAnimal;
        prevAnimal.parent = node;
        prevAnimal.isYesAnswer = false;
    }

    public void changeNoAnswer(Node node, Leaf animal) {
        Node prevAnimal = noAnswer;
        noAnswer = node;
        node.yesAnswer = animal;
        node.noAnswer = prevAnimal;
        prevAnimal.parent = node;
        prevAnimal.isYesAnswer = false;
    }
}

