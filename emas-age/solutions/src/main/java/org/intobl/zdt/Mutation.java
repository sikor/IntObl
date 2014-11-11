package org.intobl.zdt;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.jage.solution.ISolution;
import org.jage.variation.mutation.IMutateSolution;

import javax.inject.Inject;
import java.util.List;
import java.util.Random;

/**
 * Created by pawel on 04/11/14.
 */
public class Mutation implements IMutateSolution<ISolution> {
    private Random random = new Random();

    private ZdtProblem zdt1Problem;

    @Inject
    public void setProblem(MultivariateProblem problem) {
        this.zdt1Problem = (ZdtProblem) problem;
    }

    private Function<List<Double>, Void> swap = new Function<List<Double>, Void>() {
        public Void apply(List<Double> representation) {
            int first = random.nextInt(representation.size());
            int second = random.nextInt(representation.size());
            Double firstVal = representation.get(first);
            Double secondVal = representation.get(second);
            representation.set(first, secondVal);
            representation.set(second, firstVal);
            return null;
        }
    };

    private Function<List<Double>, Void> mutateFirst = new Function<List<Double>, Void>() {
        public Void apply(List<Double> representation) {
            representation.set(0, random.nextDouble());
            return null;
        }
    };

    private Function<List<Double>, Void> mutateOne = new Function<List<Double>, Void>() {
        public Void apply(List<Double> representation) {
            representation.set(random.nextInt(representation.size()), random.nextDouble());
            return null;
        }
    };
    ImmutableList<Function<List<Double>, Void>> mutateFunctions = ImmutableList.<Function<List<Double>, Void>>builder()
            .add(mutateFirst)
            .add(mutateOne)
            .add(swap)
            .build();

    @Override
    public void mutateSolution(ISolution solution) {
        ZdtSolution zdtSolution = (ZdtSolution) solution;
        List<Double> representation = zdtSolution.getRepresentation();

        for (int i = 0; i < 10; i++) {
            mutateFunctions.get(random.nextInt(mutateFunctions.size())).apply(representation);
        }

        zdt1Problem.calculateFitness(zdtSolution);
    }
}
