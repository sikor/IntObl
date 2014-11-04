package org.intobl.zdt;

import org.jage.evaluation.ISolutionEvaluator;
import org.jage.solution.ISolution;

import javax.inject.Inject;

/**
 * Created by pawel on 04/11/14.
 */
public class Evaluator implements ISolutionEvaluator {


    @Inject
    MultivariateProblem problem;

    @Override
    public Double evaluate(ISolution iSolution) {
        ZdtSolution solution = (ZdtSolution) iSolution;
        problem.calculateFitness(solution);
        return -solution.getFitness().getSum();
    }
}
