package core.analyze.test;

import core.analyze.AnalyzeConfig;
import core.analyze.AnalyzeResult;
import core.analyze.Analyzer;
import core.analyze.AnalyzerIterationStrategy;
import core.model.data.*;
import core.model.data.Module;

import java.util.*;

public class GraphAnalyzeTest {
    private int[] splitInteger(int n, int sum, boolean flag) {
        //随机抽取n-1个小于sum的数
        List<Integer> list = new ArrayList();
        //将0和sum加入到里list中
        list.add(0);
        //判断生成的正整数集合中是否允许为0，true元素可以为0  false元素不可以为0
        if (!flag) {
            sum = sum - n;
        }
        list.add(sum);
        int temp = 0;
        for (int i = 0; i < n - 1 ; i++) {
            temp = (int) (Math.random() * sum);
            list.add(temp);
        }
        Collections.sort(list);
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = list.get(i + 1) - list.get(i);
            if (!flag) {
                nums[i] += 1;
            }
        }
        return nums;
    }

    private Set<Node> mockNodes(int scale) {
        Set<Node> nodeSet = new HashSet<>();
        for (int i = 0; i < scale; i++) {
            nodeSet.add(new Node(i, "N" + i, null));
        }
        return nodeSet;
    }

    private Set<Edge> mockEdges(int scale, Set<Node> nodeSet) {
        Set<Edge> edgeSet = new HashSet<>();
        List<Node> nodeList = new ArrayList<>(nodeSet);
        Random r = new Random();

        for (int i = 0; i < scale; i++) {
            int startIndex = r.nextInt(nodeList.size());
            int endIndex = r.nextInt(nodeList.size() - 1);
            if (endIndex == startIndex) {
                endIndex = nodeList.size() - 1;
            }
            Node startNode = nodeList.get(startIndex);
            Node endNode = nodeList.get(endIndex);
            Edge e = new Edge(startNode, endNode, r.nextInt(15) + 1);
            edgeSet.add(e);
        }
        return edgeSet;
    }

    private Set<Module> mockInitNodeModules(int moduleNumber, Set<Node> nodeSet) {
        Set<Module> modules = new HashSet<>();

        List<Node> nodeList = new ArrayList<>(nodeSet);
        int[] splitRange = splitInteger(moduleNumber, nodeList.size(), false);

        int currentIndex = 0;
        for (int i = 0; i < splitRange.length; i++) {
            Set<Node> moduleNodes = new HashSet<>();
            for (int j = 0; j < splitRange[i]; j++) {
                moduleNodes.add(nodeList.get(currentIndex));
                currentIndex++;
            }
            Module ng = new Module("NG" + i, moduleNodes, true);
            for (Node node : moduleNodes) {
                node.setModule(ng);
            }
            modules.add(ng);
        }
        return modules;
    }

    @org.junit.jupiter.api.Test
    void testGraphAnalyze() {
        // 构建数据
        Set<Node> nodeSet = mockNodes(35);
        Set<Edge> edgeSet = mockEdges(120, nodeSet);
        Set<Module> modules = mockInitNodeModules(7, nodeSet);

        Graph initGraph = new Graph(nodeSet, edgeSet, modules);
        Analyzer analyzer = new Analyzer(initGraph, new AnalyzeConfig(AnalyzerIterationStrategy.FIX, 3));
        AnalyzeResult result = analyzer.executeAnalyze();
        result.showIterationInfo();
    }
}
