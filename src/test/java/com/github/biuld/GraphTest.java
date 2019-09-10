package com.github.biuld;

import com.github.biuld.graph.Edge;
import com.github.biuld.graph.Graph;
import com.github.biuld.graph.Topo;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.*;

public class GraphTest {

    @Test
    public void test() throws InterruptedException {
        Set<Edge<String>> edgeSet = Set.of(new Edge<String>("语文", "数学"),
                new Edge<String>("1", "2"),
                new Edge<String>("0", "2"),
                new Edge<String>("2", "3"),
                new Edge<String>("1", "3"),
                new Edge<String>("1", "4"),
                new Edge<String>("1", "5"),
                new Edge<String>("1", "6"),
                new Edge<String>("1", "7"),
                new Edge<String>("1", "8")
        );

        Graph<String> graph = Graph.apply(edgeSet);

        Topo<String> topo = new Topo<>((ThreadPoolExecutor) Executors.newFixedThreadPool(8));
        topo.topoSortAllCon(graph);
//        topo.topoSortAllFlux(graph);
    }
}
