package com.github.biuld.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Graph<V> {

    private Set<V> vertexSet;
    private Set<Edge<V>> edgeSet;

    public Graph() {
    }

    public Graph(Set<V> vertexSet, Set<Edge<V>> edgeSet) {
        this.vertexSet = vertexSet;
        this.edgeSet = edgeSet;
    }

    public Set<Edge<V>> getEdgeSet() {
        return edgeSet;
    }

    public Set<V> getVertexSet() {
        return vertexSet;
    }

    public Graph<V> removeVertex(V vertex) {
        Set<V> vertexSet = this.vertexSet.stream().filter(elem -> !elem.equals(vertex)).collect(Collectors.toSet());
        Set<Edge<V>> edgeSet = this.edgeSet.stream()
                .filter(elem -> !elem.getSource().equals(vertex))
                .filter(elem -> !elem.getTarget().equals(vertex))
                .collect(Collectors.toSet());

        return new Graph<V>(vertexSet, edgeSet);
    }

    private long countInEdges(V vertex) {
        return this.edgeSet.stream().filter(elem -> elem.getTarget().equals(vertex)).count();
    }

    public Set<V> getRoots() {
        return this.vertexSet.stream().filter(elem -> countInEdges(elem) == 0).collect(Collectors.toSet());
    }

    public static <V> Graph<V> apply(Set<Edge<V>> edgeSet) {
        Set<V> vertexSet = new HashSet<>();
        edgeSet.forEach(elem -> {
            vertexSet.add(elem.getSource());
            vertexSet.add(elem.getTarget());
        });

        return new Graph<V>(vertexSet, edgeSet);
    }
}
