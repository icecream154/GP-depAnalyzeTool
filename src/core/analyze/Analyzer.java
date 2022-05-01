package core.analyze;

import core.analyze.engine.ModuleSpecificationAnalyzer;
import core.analyze.engine.NodePartitionAnalyzer;
import core.analyze.iteration.IterationAnalyzeResult;
import core.analyze.statics.NodeToModuleDependency;
import core.model.data.Graph;
import core.model.data.Module;
import core.model.data.Node;
import core.model.specification.ModuleSpecification;

import java.util.*;

public class Analyzer {
    private final Graph originalGraph;
    private final AnalyzeConfig analyzeConfig;

    public Analyzer(Graph originalGraph, AnalyzeConfig analyzeConfig) {
        this.originalGraph = originalGraph;
        this.analyzeConfig = analyzeConfig;
    }

    public AnalyzeResult executeAnalyze() {
        ModuleSpecificationAnalyzer moduleSpecificationAnalyzer = new ModuleSpecificationAnalyzer();
        NodePartitionAnalyzer nodePartitionAnalyzer = new NodePartitionAnalyzer();

        assert (analyzeConfig.getIterationStrategy() == AnalyzerIterationStrategy.FIX);

        boolean needNextIteration = true;
        int iterationTime = 0;
        List<IterationAnalyzeResult> iterationAnalyzeResultList = new ArrayList<>();
        Graph iterationGraph = originalGraph;

        int iterationIndex = 1;
        while (needNextIteration) {
            System.out.println("Iteration[" + iterationIndex + "] " +
                    " Start");

            iterationGraph = iterationGraph.clone();
            IterationAnalyzeResult iterationAnalyzeResult = new IterationAnalyzeResult(iterationIndex, iterationGraph);
            NodeToModuleDependency.register(iterationGraph);
            System.out.println("Iteration[" + iterationIndex + "]: " +
                    " Node to Module Dependency Register Success");

            /*
            分析阶段：
            对于一个模块M中所有节点，给出每一个[节点]对其他所有[模块]的依赖组成的向量V。
            分析所有节点的向量V，使用节点ModuleDependency向量模式发现算法[A2]，输出以下信息：
                    ①模块外部依赖模式[ModuleSpecification]，模式包含该模块对于其他每一个模块依赖区间与依赖均值两个向量。
            依赖区间向量：<[0.2-0.3], [0.4-0.6], [0-0.1]>
            依赖均值向量：<0.27, 0.52, 0.05>
                    ②符合ModuleSpecification的节点列表(ModuleSpecification的区间就由该列表的极值决定)
                    ③不符合ModuleSpecification的节点列表
                    ④ModuleSpecification的收敛水平(平均区间差异——上述ModuleSpecification非零平均区间差异为(0.1+0.2+0.1)/3=0.133)
            定义阈值α=0.2，β=0.4
                    - 收敛：平均区间差异<=α
                    - 分散：α<平均区间差异<=β
                    - 稀疏：平均区间差异>β

            说明：采用向量对模块内的节点进行整体分析，而不对每一个模块单独分析，原因在于：同一节点组的外部依赖是否类似取决于架构设计。
            如果节点组内的节点依赖整体趋同，比如都属于架构中的某一层，那么ModuleSpecification会大概率被判定为收敛，算法能够发现不匹配模式的节点；
            而如果节点组内的节点对于外部的依赖本身较为稀疏，那么在设计中该模块本身的职责可能尚未明确，分析得到的模式并无太大的实际含义。
            */

            Set<ModuleSpecification> accurateSpecifications = new HashSet<>();
            Set<ModuleSpecification> normalSpecifications = new HashSet<>();
            Set<ModuleSpecification> looseSpecifications = new HashSet<>();
            Set<Node> unClassifiedNodes = new HashSet<>();

            for (Module module : iterationGraph.getModules()) {
                Set<Module> otherModules = new HashSet<>(iterationGraph.getModules());
                otherModules.remove(module);
                ModuleSpecification moduleSpecification = moduleSpecificationAnalyzer.
                        analyzeModulePattern(module, new ArrayList<>(otherModules));
                unClassifiedNodes.addAll(moduleSpecification.getAbnormalNodeSet());
                switch (moduleSpecification.getAccuracy()) {
                    case LOOSE -> looseSpecifications.add(moduleSpecification);
                    case NORMAL -> normalSpecifications.add(moduleSpecification);
                    case ACCURATE -> accurateSpecifications.add(moduleSpecification);
                }
                System.out.println("Iteration[" + iterationIndex + "]: " +
                        " Module[" + module.getName() + "] Specification Analyze Success");
            }

            /*
            变更阶段：
            节点与ModuleSpecification的距离计算公式：<实际依赖向量> 到 <目标模块依赖均值向量> 的欧几里得距离。(不考虑到待变更节点所在的模块)
                ①对于不符合ModuleSpecification的所有节点，遍历匹配所有[收敛]的ModuleSpecification。如果成功匹配，变更该节点所属的模块。
                ②对于依旧尚未匹配模块的节点，遍历匹配所有[分散]的ModuleSpecification。如果成功匹配，变更该节点所属的模块。
                ③对于依旧尚未匹配模块的节点，遍历计算到所有[稀疏]ModuleSpecification的距离，如果成功匹配，变更该节点所属的模块。
                *④剩余的节点使用稀疏节点模块划分算法[A3]，得到新的若干模块。
            */

            for (Node unclassifiedNode : unClassifiedNodes) {
                boolean classified = false;
                for (ModuleSpecification specification : accurateSpecifications) {
                    if (specification.matchSpecification(unclassifiedNode).isMatch()) {
                        unclassifiedNode.setModule(specification.getModule());
                        classified = true;
                        break;
                    }
                }

                if (!classified) {
                    for (ModuleSpecification specification : normalSpecifications) {
                        if (specification.matchSpecification(unclassifiedNode).isMatch()) {
                            unclassifiedNode.setModule(specification.getModule());
                            classified = true;
                            break;
                        }
                    }
                }

                if (!classified) {
                    for (ModuleSpecification specification : looseSpecifications) {
                        if (specification.matchSpecification(unclassifiedNode).isMatch()) {
                            unclassifiedNode.setModule(specification.getModule());
                            break;
                        }
                    }
                }
            }
            System.out.println("Iteration[" + iterationIndex + "]: " +
                    " Unclassifed Nodes Match Over");

            // 使用稀疏节点划分算法，重新生成模块
            List<Module> referenceModules = new ArrayList<>();
            for (ModuleSpecification specification : accurateSpecifications) {
                referenceModules.add(specification.getModule());
            }
            for (ModuleSpecification specification : normalSpecifications) {
                referenceModules.add(specification.getModule());
            }

            Set<Node> sparseNodes = new HashSet<>(unClassifiedNodes);
            for (ModuleSpecification specification : looseSpecifications) {
                sparseNodes.addAll(specification.getSpecificationNodeSet());
            }
            System.out.println("Iteration[" + iterationIndex + "]: " +
                    " SparseNodes Collection Size " + sparseNodes.size());

            Set<Module> newModules = nodePartitionAnalyzer.partitionSparseNodes(sparseNodes, referenceModules);
            iterationGraph.getModules().addAll(newModules);

            // 清除无任何节点的模块
            Set<Module> emptyModules = iterationGraph.clearEmptyModules();
            iterationAnalyzeResult.setEmptyModules(emptyModules);

            System.out.println("Iteration[" + iterationIndex + "]: " +
                    " New " + newModules.size() + " Modules Generate Success");

            // 增加迭代次数，并生成本次迭代结果
            iterationTime++;
            iterationAnalyzeResultList.add(iterationAnalyzeResult);

            needNextIteration = iterationTime < analyzeConfig.getFixIteration();
            iterationIndex++;
        }

        return new AnalyzeResult(iterationGraph.clone(), iterationTime, iterationAnalyzeResultList);
    }


}
