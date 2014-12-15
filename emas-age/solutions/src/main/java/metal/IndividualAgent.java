package metal;

import jmetal.core.Problem;
import jmetal.core.Variable;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.JMException;

import java.util.Random;

/**
 * Created by pawel on 09/12/14.
 */
public class IndividualAgent {

    private Double[] variables;
    private ArrayReal arrayReal;
    private int energy = 0;

    private int congestedNeighbors = 0;
    private boolean elite;

    public IndividualAgent(ArrayReal copy) {
        arrayReal = copy;
        variables = arrayReal.array_;
    }

    public IndividualAgent(int size, Random random, Problem problem) throws JMException {
        arrayReal = new ArrayReal(size, problem);
        variables = arrayReal.array_;
        for (int i = 0; i < size; ++i) {
            variables[i] = random.nextDouble();
        }
    }

    public int energy() {
        return energy;
    }

    public Double[] getDoubleArray() {
        return variables;
    }

    public Variable[] getWrappedArrayReal() {
        Variable[] variable = new Variable[1];
        variable[0] = arrayReal;
        return variable;
    }

    public void changeEnergy(int i) {
        energy += i;
    }

    public ArrayReal getArrayReal() {
        return arrayReal;
    }

    public int getCongestedNeighbors() {
        return congestedNeighbors;
    }

    public void addCongestedNeighbor() {
        congestedNeighbors++;
    }

    public void clearCongestedNeighbors() {
        congestedNeighbors = 0;
    }

    public boolean isElite() {
        return elite;
    }

    public void setElite(boolean elite) {
        this.elite = elite;
    }
}
