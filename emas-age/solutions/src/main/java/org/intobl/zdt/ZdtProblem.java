package org.intobl.zdt;

import java.util.List;

public abstract class ZdtProblem implements MultivariateProblem {
    protected final int N;

    public ZdtProblem(int n) {
        N = n;
    }

    @Override
    public void calculateFitness(SolutionWithFitness solutionWithFitness) {
        ZdtSolution solution = (ZdtSolution) solutionWithFitness;
        List<Double> representation = solution.getRepresentation();
        List<Double> fitness = getFitness(representation);
        solution.getFitness().setValues(fitness);
    }

    protected abstract List<Double> getFitness(List<Double> representation);

    public int getN() {
        return N;
    }

    public abstract SolutionsList getSolution();
}
