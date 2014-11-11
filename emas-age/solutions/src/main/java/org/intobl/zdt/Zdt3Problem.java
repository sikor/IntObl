package org.intobl.zdt;

import java.util.List;

public class Zdt3Problem extends ZdtProblem {
    public Zdt3Problem(int n) {
        super(n);
    }

    protected List<Double> getFitness(List<Double> representation) {
        return ZdtHelpers.zdt3Fitness(representation);
    }
}
