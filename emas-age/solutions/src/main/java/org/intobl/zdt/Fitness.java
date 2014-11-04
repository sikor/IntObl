package org.intobl.zdt;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pawel on 04/11/14.
 */
public class Fitness {

    private List<Double> values;


    public List<Double> getValues() {
        return values;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }

    public void set(Double val1, Double val2) {
        this.values = Arrays.asList(val1, val2);
    }


    public boolean dominates(Fitness other) {
        boolean isBetter = false;
        for (int i = 0; i < getValues().size(); ++i) {
            Double thisVal = getValues().get(i);
            Double otherVal = other.getValues().get(i);
            if (thisVal < otherVal) {
                return false;
            }
            if (thisVal > otherVal) {
                isBetter = true;
            }
        }
        return isBetter;
    }

}
