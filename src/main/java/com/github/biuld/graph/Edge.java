package com.github.biuld.graph;

import java.util.Map;

public class Edge<T> {
    private T source;
    private T target;

    public Edge(T source, T target) {
        this.source = source;
        this.target = target;
    }

    public Edge() {}

    public T getSource() {
        return source;
    }

    public T getTarget() {
        return target;
    }
}
