package org.intobl.zdt;

import org.jage.evaluation.ISolutionEvaluator;
import org.jage.solution.ISolution;

/**
 * Created by pawel on 04/11/14.
 */
public class Evaluator implements ISolutionEvaluator {

    @Override
    public Double evaluate(ISolution iSolution) {
        ZdtSolution solution = (ZdtSolution) iSolution;
        return solution.getFitness().getSum();
    }
}
