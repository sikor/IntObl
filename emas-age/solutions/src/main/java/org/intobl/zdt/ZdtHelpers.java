package org.intobl.zdt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawel on 04/11/14.
 */
public class ZdtHelpers {

    public static final int N = 30;


    public static List<Double> zdt1Fitness(List<Double> representation) {
        List<Double> result = new ArrayList<Double>(2);

        double f1 = representation.get(0);

        double tailSum = 0;
        for (int i = 1; i < representation.size(); ++i) {
            tailSum += representation.get(i);
        }

        double g = 1 + (9 / (representation.size() - 1)) * tailSum;

        double f2 = g * zdt1H(f1, g);

        result.add(f1);
        result.add(f2);

        return result;
    }

    private static double zdt1H(double f1, double g) {
        return 1 - Math.sqrt(f1 / g);
    }

    private ZdtHelpers() {

    }
}
