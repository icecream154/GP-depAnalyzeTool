package core.analyze.iteration;

import core.analyze.statics.NodeToModuleDependency;
import core.model.data.Graph;
import core.model.data.Module;
import core.model.specification.ModuleSpecification;

import java.util.Set;

public class IterationAnalyzeResult {
    private int iterationIndex;
    private Graph iterationGraph;
    private Set<Module> emptyModules;

    public IterationAnalyzeResult(int iterationIndex, Graph iterationGraph) {
        this.iterationIndex = iterationIndex;
        this.iterationGraph = iterationGraph;
    }

    public int getIterationIndex() {
        return iterationIndex;
    }

    public Graph getIterationGraph() {
        return iterationGraph;
    }

    public Set<Module> getEmptyModules() {
        return emptyModules;
    }

    public void setEmptyModules(Set<Module> emptyModules) {
        this.emptyModules = emptyModules;
    }

    public String getIterationAnalyzeResultInfo() {
        String result = "Iteration[" + iterationIndex + "]:\n";
        for (Module module : iterationGraph.getModules()) {
            result += getModuleIterationInfo(module);
        }
        result += "\t[--Removed Modules--]\n";
        for (Module module : emptyModules) {
            result += getModuleIterationInfo(module);
        }
        return result;
    }

    private String getModuleIterationInfo(Module module) {
        String result = "\tModule[" + module.getName() + "]:\n";
        result += "\t\tCurrentNodes: " + module.getNodes() + "\n";
        ModuleSpecification ms = module.getModuleSpecification();
        if (ms == null) {
            result += "\t\tSpecification: null\n";
        } else {
            result += "\t\tSpecification: \n" +
                    "\t\t\tnodeDependencyVectorMap=" + NodeToModuleDependency.getNodeDependencyVectorInfo(4, ms.getNodeDependencyVectorMap()) + ",\n" +
                    "\t\t\tspecificationNodeSet=" + ms.getSpecificationNodeSet() + ",\n" +
                    "\t\t\tabnormalNodeSet=" + ms.getAbnormalNodeSet() + ",\n" +
                    "\t\t\tmoduleAverageDependencyMap=" + NodeToModuleDependency.getDependencyMapVectorInfo(ms.getModuleAverageDependencyMap()) + ",\n" +
                    "\t\t\tmoduleMaxDependencyMap=" + NodeToModuleDependency.getDependencyMapVectorInfo(ms.getModuleMaxDependencyMap()) + ",\n" +
                    "\t\t\tmoduleMinDependencyMap=" + NodeToModuleDependency.getDependencyMapVectorInfo(ms.getModuleMinDependencyMap()) + ",\n" +
                    "\t\t\tnonZeroDependencyAverageRange=" + ms.getNonZeroDependencyAverageRange() + ",\n" +
                    "\t\t\tnonZeroRangeSum=" + ms.getNonZeroRangeSum() + "\n" +
                    "\t\t\tnonZeroRangeCount=" + ms.getNonZeroRangeCount() + "\n" +
                    "\t\t\taccuracy=" + ms.getAccuracy() + "\n" +
                    "\t\t}\n";
        }
        return result;
    }
}
