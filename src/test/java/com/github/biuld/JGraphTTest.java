package com.github.biuld;

import com.github.biuld.jgrapht.Edge;
import com.github.biuld.jgrapht.Topo;
import org.junit.Test;

import java.util.List;

/**
 * Created by biuld on 2019/8/29.
 */
public class JGraphTTest {

    @Test
    public void test() {

        Topo topo = new Topo();

        topo.addEdge(new Edge("程设", "JAVA"));
        topo.addEdge(new Edge("JAVA", "计组"));
        topo.addEdge(new Edge("JAVA", "数据结构"));
        topo.addEdge(new Edge("计组", "计算机网络"));
        topo.addEdge(new Edge("数据结构", "计算机网络"));
        topo.addEdge(new Edge("计组", "操作系统"));
        topo.addEdge(new Edge("数据结构", "操作系统"));

        List.of(0, 1, 2).forEach(elem ->
                System.out.println(String.join(" -> ", topo.sort(elem))
                ));
    }
}
