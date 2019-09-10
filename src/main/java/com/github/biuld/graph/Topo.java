package com.github.biuld.graph;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Topo<V> {

    private ThreadPoolExecutor ec = null;

    private final List<List<V>> result = new LinkedList<>();

    public Topo(ThreadPoolExecutor ec) {
        this.ec = ec;
    }

    private void sort(Graph<V> graph, List<V> acc) {
        Set<V> roots = graph.getRoots();

        if (roots.isEmpty()) {
            if (graph.getEdgeSet().isEmpty()) {
                synchronized (result) {
                    result.add(acc);
                }
            } else
                throw new IllegalArgumentException("The input graph has at least one cycle");
        } else {
            roots.forEach(elem -> {
                List<V> accNeo = new LinkedList<>(acc);
                accNeo.add(elem);

                ec.execute(() -> sort(graph.removeVertex(elem), accNeo));
            });
        }
    }

    public List<List<V>> topoSortAllCon(Graph<V> toSort) throws InterruptedException {

        long start = System.currentTimeMillis();

        sort(toSort, List.of());
        while (true) {
            Thread.sleep(1000);
            if (ec.getActiveCount() == 0) {
                ec.shutdownNow();
                break;
            }
        }

        System.out.println("completed after " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
        System.out.println("got " + result.size() + " results");

        return result;
    }

    private List<GraphPak<V>> emitter(GraphPak<V> pak) {

        Graph<V> graph = pak.getGraph();
        List<V> acc = pak.getAcc();

        Set<V> roots = graph.getRoots();

        if (roots.isEmpty()) {
            if (graph.getEdgeSet().isEmpty())
                return List.of(GraphPak.apply(graph, acc, true));
        }

        return roots.stream().map(elem -> {
            List<V> accNeo = new LinkedList<>(acc);
            accNeo.add(elem);
            return GraphPak.apply(graph.removeVertex(elem), accNeo);
        }).collect(Collectors.toList());
    }

    public List<List<V>> topoSortAllFlux(Graph<V> graph) {
        long start = System.currentTimeMillis();

        List<List<V>> result = Flux.fromIterable(emitter(GraphPak.apply(graph, List.of())))
                .expand(elem -> {
                    if (!elem.isCompleted())
                        return Flux.fromIterable(emitter(elem)).subscribeOn(Schedulers.elastic());

                    return Flux.empty();
                })
                .subscribeOn(Schedulers.elastic())
                .filter(GraphPak::isCompleted)
                .map(GraphPak::getAcc)
                .collectList()
                .block();

        System.out.println("completed after " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
        System.out.println("got " + result.size() + " results");

        return result;
    }
}
