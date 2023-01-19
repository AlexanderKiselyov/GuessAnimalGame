package org.example;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BinaryTree {
    private final Node head;

    public BinaryTree() {
        Node initialAnswer = new Node("живет на суше", null, false);
        Leaf cat = new Leaf("кот", initialAnswer, true);
        Leaf whale = new Leaf("кит", initialAnswer, false);
        initialAnswer.setYesAnswer(cat);
        initialAnswer.setNoAnswer(whale);
        head = initialAnswer;
    }

    public BinaryTree(Map<Long, Row> rows) throws SQLException {
        Map<Long, Node> nodes = new HashMap<>();
        for (Map.Entry<Long, Row> row : rows.entrySet()) {
            if (Objects.equals(row.getValue().getQuestion(), "null")) {
                Leaf leaf = new Leaf(row.getValue().getAnimal(), nodes.get(row.getKey() / 2), row.getKey() % 2 == 0);
                nodes.put(row.getKey(), leaf);
            } else {
                Node node = new Node(row.getValue().getQuestion(), nodes.get(row.getKey() / 2), row.getKey() % 2 == 0);
                nodes.put(row.getKey(), node);
            }
        }
        for (Map.Entry<Long, Node> node : nodes.entrySet()) {
            if (nodes.containsKey(2L * node.getKey())) {
                node.getValue().setYesAnswer(nodes.get(2L * node.getKey()));
                node.getValue().setNoAnswer(nodes.get(2L * node.getKey() + 1));
            }
        }
        head = nodes.get(1L);
    }

    public Node getHead() {
        return head;
    }

    public TreePair getTreePair() {
        return new TreePair(getDepth(head), getMaxNodeLength(head, 0));
    }

    private Integer getDepth(Node node) {
        if (node != null) {
            return 1 + Math.max(getDepth(node.getYesAnswer()), getDepth(node.getNoAnswer()));
        } else {
            return 0;
        }
    }

    private Integer getMaxNodeLength(Node node, Integer currentMaxLength) {
        if (node != null) {
            if (node instanceof Leaf) {
                currentMaxLength = Math.max(((Leaf) node).getAnimal().length(), currentMaxLength);
            } else {
                currentMaxLength = Math.max(node.getQuestion().length(), currentMaxLength);
            }
            return Math.max(getMaxNodeLength(node.getYesAnswer(), currentMaxLength), getMaxNodeLength(node.getNoAnswer(), currentMaxLength));
        } else {
            return currentMaxLength;
        }
    }
}
