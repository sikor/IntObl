package org.intobl.zdt;

import org.jage.random.INormalizedDoubleRandomGenerator;
import org.jage.solution.ISolution;
import org.jage.solution.ISolutionFactory;
import org.jage.solution.IVectorSolution;

import javax.inject.Inject;

/**
 * Created by pawel on 04/11/14.
 */
public class SolutionFactory implements ISolutionFactory {

    @Inject
    private INormalizedDoubleRandomGenerator rand;

    @Inject
    ZdtProblem problem;

    @Override
    public ISolution createEmptySolution() {
        return createInitializedSolution();
    }

    @Override
    public ISolution createInitializedSolution() {
        ZdtSolution solution = new ZdtSolution();
        for (int i = 0; i < problem.getN(); ++i) {
            solution.getRepresentation().add(rand.nextDouble());
        }
        problem.calculateFitness(solution);
        return solution;
    }

    @Override
    public ISolution copySolution(ISolution iSolution) {
        IVectorSolution<Double> solution = (IVectorSolution<Double>) iSolution;
        ZdtSolution newSolution = new ZdtSolution();
        for (int i = 0; i < solution.getRepresentation().size(); ++i) {
            newSolution.getRepresentation().add(solution.getRepresentation().get(i));
        }
        problem.calculateFitness(newSolution);
        return newSolution;
    }
}
