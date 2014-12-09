package metal;

import jmetal.util.JMException;

import java.util.Random;

/**
 * Created by pawel on 04/11/14.
 */
public class Recombination {

    private Random random = new Random();


    public void recombine(Double[] first, Double[] second) throws JMException {
        for (int i = 0; i < first.length; i++) {
            double a, b;
            if (random.nextBoolean()) {
                a = first[i];
                b = second[i];
            } else {
                b = first[i];
                a = second[i];
            }
            first[i] = a;
            second[i] = b;
        }
    }
}
