package metal;

import com.google.common.base.Preconditions;
import jmetal.core.Solution;

/**
 * Created by pawel on 09/12/14.
 */
public class IndividualAgent {

    private final Solution solution;

    private int energy = 0;
    private int prestige = 0;

    private int meetingsCount = 0;
    private int congestedNeighbors = 0;
    private double averageCongestionOfOthers = 0.0;

    private boolean elite;

    public IndividualAgent(Solution copy, int initialEnergy) {
        solution = copy;
        changeEnergy(initialEnergy);
    }

    public int energy() {
        return energy;
    }


    public Solution copySolution() {
        return new Solution(solution);
    }

    private void changeEnergy(int i) {
        if (energy + i < 0) {
            throw new IllegalStateException("Can't take so much energy!");
        }
        energy += i;
    }

    public void migrate(Island from, Island to, int migrationEnergyChange) {
        from.sendAgentToOtherIsland(this, to);
        changeEnergy(migrationEnergyChange);
    }

    public void giveEnergy(IndividualAgent target, int amount) {
        Preconditions.checkArgument(amount >= 0);
        Preconditions.checkNotNull(target);
        target.changeEnergy(amount);
        this.changeEnergy(-amount);
    }

    public void meet(IndividualAgent other) {
        averageCongestionOfOthers = (averageCongestionOfOthers * meetingsCount + other.getCongestedNeighbors()) / (meetingsCount + 1);
        meetingsCount++;
    }

    public int getCongestedNeighbors() {
        return congestedNeighbors;
    }

    public double getAverageCongestionOfOthers() {
        return averageCongestionOfOthers;
    }

    public void addCongestedNeighbor() {
        congestedNeighbors++;
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
        ++prestige;
    }


    public int getPrestige() {
        return prestige;
    }
}
