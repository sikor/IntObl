package org.intobl.zdt;

import org.jage.solution.IVectorSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawel on 04/11/14.
 */
public class ZdtSolution extends SolutionWithFitness implements IVectorSolution<Double> {

    private final List<Double> representation = new ArrayList<Double>();

    @Override
    public List<Double> getRepresentation() {
        return representation;
    }
}
