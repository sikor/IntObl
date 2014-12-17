package metal;

import jmetal.core.Solution;

/**
 * Created by pawel on 09/12/14.
 */
public class IndividualAgent {

    private final Solution solution;

    private int energy = 0;
    private int congestedNeighbors = 0;
    private boolean elite;
    private int wonInARow = 0;

    public IndividualAgent(Solution copy) {
        solution = copy;
    }

    public int energy() {
        return energy;
    }


    public Solution copySolution() {
        return new Solution(solution);
    }

    public void changeEnergy(int i) {
        if (energy + i < 0) {
            throw new IllegalStateException("Can't take so much energy!");
        }
        energy += i;
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

    public Solution getSolution() {
        return solution;
    }

    public void won() {
        ++wonInARow;
    }

    public void loose() {
        wonInARow = 0;
    }

    public int getWonInARow() {
        return wonInARow;
    }
}
