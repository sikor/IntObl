package org.intobl.zdt;

import org.jage.solution.IVectorSolution;

import java.util.ArrayList;
import java.util.List;

public class ZdtSolution extends SolutionWithFitness implements IVectorSolution<Double> {

    private final List<Double> representation = new ArrayList<Double>();

    @Override
    public List<Double> getRepresentation() {
        return representation;
    }

    @Override
    public String toString() {
        return String.format("Fitness: %s\n representation: %s", getFitness(), representation.toString());
    }
}
