package com.github.biuld.graph;

import java.util.List;

public class GraphPak<V> {

    private Graph<V> graph;
    private List<V> acc;
    private boolean isCompleted;

    public static <V> GraphPak<V> apply(Graph<V> graph, List<V> acc, boolean isCompleted) {
        GraphPak<V> pak = new GraphPak<>();
        pak.graph = graph;
        pak.acc = acc;
        pak.isCompleted = isCompleted;

        return pak;
    }

    public static <V> GraphPak<V> apply(Graph<V> graph, List<V> acc) {
        GraphPak<V> pak = new GraphPak<>();
        pak.graph = graph;
        pak.acc = acc;
        pak.isCompleted = false;

        return pak;
    }


    public Graph<V> getGraph() {
        return graph;
    }

    public void setGraph(Graph<V> graph) {
        this.graph = graph;
    }

    public List<V> getAcc() {
        return acc;
    }

    public void setAcc(List<V> acc) {
        this.acc = acc;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
