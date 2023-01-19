package org.example;

public class TreePair {
    private final Integer depth;
    private final Integer maxNodeLength;

    public TreePair(Integer depth, Integer maxNodeLength) {
        this.depth = depth;
        this.maxNodeLength = maxNodeLength;
    }

    public Integer getDepth() {
        return depth;
    }

    public Integer getMaxNodeLength() {
        return maxNodeLength;
    }
}
