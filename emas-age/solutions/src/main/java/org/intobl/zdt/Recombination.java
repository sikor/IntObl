package org.intobl.zdt;

import org.jage.solution.ISolution;
import org.jage.variation.recombination.IRecombine;

import javax.inject.Inject;
import java.util.List;
import java.util.Random;

/**
 * Created by pawel on 04/11/14.
 */
public class Recombination implements IRecombine<ISolution> {

    private Random random = new Random();
    private ZdtProblem zdt1Problem;

    @Inject
    public void setProblem(MultivariateProblem problem) {
        this.zdt1Problem = (ZdtProblem) problem;
    }

    @Override
    public void recombine(ISolution first, ISolution second) {
        ZdtSolution firstZdt = (ZdtSolution) first;
        ZdtSolution secondZdt = (ZdtSolution) second;

        List<Double> firstZdtRepresentation = firstZdt.getRepresentation();
        List<Double> secondZdtRepresentation = secondZdt.getRepresentation();
        for (int i = 0; i < firstZdtRepresentation.size(); i++) {
            double a, b;
            if (random.nextBoolean()) {
                a = firstZdtRepresentation.get(i);
                b = secondZdtRepresentation.get(i);
            } else {
                b = firstZdtRepresentation.get(i);
                a = secondZdtRepresentation.get(i);
            }
            firstZdtRepresentation.set(i, a);
            secondZdtRepresentation.set(i, b);
        }

        zdt1Problem.calculateFitness(firstZdt);
        zdt1Problem.calculateFitness(secondZdt);
    }
}
