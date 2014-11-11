package org.intobl.zdt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawel on 04/11/14.
 */
public class ZdtHelpers {

    public static final int N = 30;

    private abstract static class ZdtFunctionH {
        public abstract double apply(double f1, double g);
    }

    private static ZdtFunctionH zdt1h = new ZdtFunctionH() {
        @Override
        public double apply(double f1, double g) {
            return 1 - Math.sqrt(f1 / g);
        }
    };

    private static ZdtFunctionH zdt2h = new ZdtFunctionH() {
        @Override
        public double apply(double f1, double g) {
            return 1 - Math.pow(f1 / g, 2);
        }
    };

    private static ZdtFunctionH zdt3h = new ZdtFunctionH() {
        @Override
        public double apply(double f1, double g) {
            return 1 - Math.sqrt(f1 / g) - (f1 / g) * Math.sin(10 * Math.PI * f1);
        }
    };

    public static List<Double> zdt1Fitness(List<Double> representation) {
        return zdtFitness(representation, zdt1h);
    }

    public static List<Double> zdt2Fitness(List<Double> representation) {
        return zdtFitness(representation, zdt2h);
    }

    public static List<Double> zdt3Fitness(List<Double> representation) {
        return zdtFitness(representation, zdt3h);
    }

    public static List<Double> zdtFitness(List<Double> representation, ZdtFunctionH h) {
        List<Double> result = new ArrayList<Double>(2);

        double f1 = representation.get(0);

        double tailSum = 0;
        for (int i = 1; i < representation.size(); ++i) {
            tailSum += representation.get(i);
        }

        double g = 1 + (9 / (representation.size() - 1)) * tailSum;

        double f2 = g * h.apply(f1, g);

        result.add(f1);
        result.add(f2);

        return result;
    }

    private ZdtHelpers() {

    }
}
