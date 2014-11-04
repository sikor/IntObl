package org.intobl.zdt;

import java.util.List;

/**
 * Created by pawel on 04/11/14.
 */
public class Zdt1Problem implements IProblem {

    @Override
    public void calculateFitness(SolutionWithFitness solutionWithFitness) {
        ZdtSolution solution = (ZdtSolution) solutionWithFitness;
        List<Double> representation = solution.getRepresentation();
        List<Double> fitness = ZdtHelpers.zdt1Fitness(representation);
        solution.getFitness().setValues(fitness);
    }
}
