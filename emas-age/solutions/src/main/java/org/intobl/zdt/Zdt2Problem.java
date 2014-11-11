package org.intobl.zdt;

import java.util.List;

public class Zdt2Problem extends ZdtProblem {
    public Zdt2Problem(int n) {
        super(n);
    }

    protected List<Double> getFitness(List<Double> representation) {
        return ZdtHelpers.zdt2Fitness(representation);
    }
}
