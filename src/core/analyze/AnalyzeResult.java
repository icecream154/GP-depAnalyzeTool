package core.analyze;

import core.analyze.iteration.IterationAnalyzeResult;
import core.analyze.output.ModuleJsonOutput;
import core.analyze.output.ModuleNodeAdvise;
import core.analyze.output.ProjectJsonOutput;
import core.analyze.output.ProjectStatics;
import core.model.data.Graph;
import core.model.data.Module;
import core.model.specification.ModuleSpecification;
import core.model.specification.ModuleSpecificationAccuracy;
import core.model.specification.ModuleSpecificationAccurayContribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AnalyzeResult {
    private final Analyzer analyzer;
    private final Graph resultGraph;
    private final int iterationTime;
    private final List<IterationAnalyzeResult> iterationAnalyzeResultList;

    public AnalyzeResult(Analyzer analyzer, Graph resultGraph,
                         int iterationTime, List<IterationAnalyzeResult> iterationAnalyzeResultList) {
        this.analyzer = analyzer;
        this.resultGraph = resultGraph;
        this.iterationTime = iterationTime;
        this.iterationAnalyzeResultList = iterationAnalyzeResultList;
    }

    public Graph getResultGraph() {
        return resultGraph;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public int getIterationTime() {
        return iterationTime;
    }

    public List<IterationAnalyzeResult> getIterationAnalyzeResultList() {
        return iterationAnalyzeResultList;
    }

    @Override
    public String toString() {
        return "AnalyzeResult{\n" +
                "resultGraph=" + resultGraph + ",\n" +
                "iterationTime=" + iterationTime + ",\n" +
                "iterationAnalyzeResultList=" + iterationAnalyzeResultList + "\n" +
                '}';
    }

    public void showIterationInfo() {
        System.out.println("Analyze Task: [" + analyzer.getAnalyzeTaskType() + "]");
        for (IterationAnalyzeResult iterationAnalyzeResult : iterationAnalyzeResultList) {
            System.out.println(iterationAnalyzeResult.getIterationAnalyzeResultInfo());
        }
    }

    public ProjectJsonOutput buildJsonOutput() {
        IterationAnalyzeResult lastIterationResult =
                iterationAnalyzeResultList.get(iterationAnalyzeResultList.size() - 1);

        Set<Module> finalModules = lastIterationResult.getIterationGraph().getModules();
        ModuleJsonOutput[] moduleJsonOutputs = new ModuleJsonOutput[finalModules.size()];

        ProjectStatics statics = new ProjectStatics();
        statics.totalModules = finalModules.size();
        statics.totalNodes = lastIterationResult.getIterationGraph().getNodes().size();

        int i = 0;
        int specificationCount = 0;
        double accuraySum = 0;
        double accurateAccuraySum = 0;
        double normalAccuraySum = 0;
        double looseAccuraySum = 0;
        for (Module module : finalModules) {
            if (module.isFromInitGraph()) {
                statics.originalModules += 1;
            } else {
                statics.newModules += 1;
                statics.newModuleNodes += module.getNodes().size();
            }

            ModuleSpecification specification = module.getModuleSpecification();
            if (specification != null) {
                // 原有的模块
                specificationCount++;
                accuraySum += specification.getNonZeroDependencyAverageRange();
                if (specification.getAccuracy() == ModuleSpecificationAccuracy.ACCURATE) {
                    statics.accurateSpecifiations += 1;
                    statics.accurateNodes += specification.getSpecificationNodeSet().size();
                    accurateAccuraySum += specification.getNonZeroDependencyAverageRange();
                } else if (specification.getAccuracy() == ModuleSpecificationAccuracy.NORMAL) {
                    statics.normalSpecifiations += 1;
                    statics.normalNodes += specification.getSpecificationNodeSet().size();
                    normalAccuraySum += specification.getNonZeroDependencyAverageRange();
                } else if (specification.getAccuracy() == ModuleSpecificationAccuracy.LOOSE) {
                    statics.looseSpecifications += 1;
                    statics.looseNodes += specification.getSpecificationNodeSet().size();
                    looseAccuraySum += specification.getNonZeroDependencyAverageRange();
                }
                statics.abnormalNodes += specification.getAbnormalNodeSet().size();

                List<ModuleNodeAdvise> advises = new ArrayList<>();
                if (specification.getAccuracy() != ModuleSpecificationAccuracy.ACCURATE) {
                    for (ModuleSpecificationAccurayContribution contribution : specification.getAccurayContributions()) {
                        if (contribution.getAccuracyContributionRate() >= 0.3) {
                            advises.add(new ModuleNodeAdvise(contribution.getAccuracyContributionNode().getName(),
                                    contribution.getDependentModule().getName()));
                        }
                    }
                }

                ModuleNodeAdvise[] adviseArray = new ModuleNodeAdvise[advises.size()];
                for (int j = 0; j < adviseArray.length; j++) {
                    adviseArray[j] = advises.get(j);
                }
                moduleJsonOutputs[i] = new ModuleJsonOutput(
                        module.getName(), module.getPath(), module.getNodeNames(), specification.getDependentModuleNames(),
                        specification.getAccuracy().name(), specification.getUnMatchNodeNames(), specification.getMatchNodeNames(),
                        adviseArray);
            } else {
                // 新模块
                moduleJsonOutputs[i] = new ModuleJsonOutput(
                        module.getName(), module.getPath(), module.getNodeNames(), new String[0],
                        "", new String[0], new String[0], new ModuleNodeAdvise[0]);
            }
            // System.out.println("accurateAccuraySum=" + accurateAccuraySum + ", normalAccuraySum=" + normalAccuraySum + ", looseAccuraySum=" + looseAccuraySum);
            i++;
        }

        for (Module emptyModule : lastIterationResult.getEmptyModules()) {
            ModuleSpecification specification = emptyModule.getModuleSpecification();
            if (specification != null) {
                specificationCount++;
                accuraySum += specification.getNonZeroDependencyAverageRange();
                assert (specification.getAccuracy() == ModuleSpecificationAccuracy.LOOSE);
                statics.looseSpecifications += 1;
                statics.looseNodes += specification.getSpecificationNodeSet().size();
                statics.abnormalNodes += specification.getAbnormalNodeSet().size();
                looseAccuraySum += specification.getNonZeroDependencyAverageRange();
            }
        }

        if (specificationCount != 0) {
            statics.averageAccuray = accuraySum / specificationCount;
        }
        if (statics.accurateSpecifiations != 0) {
            statics.accurateAverageAccuray = accurateAccuraySum / statics.accurateSpecifiations;
        }
        if (statics.normalSpecifiations != 0) {
            statics.normalAverageAccuray = normalAccuraySum / statics.normalSpecifiations;
        }
        if (statics.looseSpecifications != 0) {
            statics.looseAverageAccuray = looseAccuraySum / statics.looseSpecifications;
        }

        return new ProjectJsonOutput(analyzer.getProjectConfig().getProjectName(),
                analyzer.getProjectConfig().getProjectLanguage(), analyzer.getProjectConfig().getProjectFrame(),
                analyzer.getAnalyzeTaskType().name(), statics, moduleJsonOutputs);
    }
}
