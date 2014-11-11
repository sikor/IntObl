package org.intobl.zdt;

import java.util.List;

/**
 * Created by pawel on 04/11/14.
 */
public class Zdt1Problem extends ZdtProblem {

    public Zdt1Problem(int n) {
        super(n);
    }

    protected List<Double> getFitness(List<Double> representation) {
        return ZdtHelpers.zdt1Fitness(representation);
    }
}
