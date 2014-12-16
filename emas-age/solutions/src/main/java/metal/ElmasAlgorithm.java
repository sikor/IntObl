package metal;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.JMException;

import java.util.*;

/**
 * Created by pawel on 09/12/14.
 */
public class ElmasAlgorithm extends Algorithm {

    private static final boolean VERBOSE = false;

    /**
     *
     */
    public static final int BATTLE_TRANSFER_ENERGY = 20;
    public static final int REPRODUCTION_PREDICATE = 90;
    public static final int CHILD_TRANSFER_ENERGY = 50;
    public static final int MIGRATION_ENERGY_CHANGE = 0;
    public static final int MIGRATION_PREDICATE = 20;
    public static final int DEAD_PREDICATE = 0;
    public static final int ELITISM_PREDICATE = 130;
    private static final double CONGESTION_LIMIT_X = 0.05; // 0.05?
    private static final double CONGESTION_LIMIT_Y = 0.02;
    private static final int CONGESTED_NEIGHBORS_LIMIT = 2;
    public static final int VIRGIN_BIRTH_ENERGY = 100;

    private Problem problem;
    private Mutation mutation = new Mutation();
    private Recombination recombination = new Recombination();
    private double migrationProb = 0.05;
    private int mutationNumber = 1;
    private int iterationsNumber = 4000;
    private int initialAgentsNumber = 200;
    private int islandsNumber = 5;
    private int eliteIslandsNumber = 3;

    private int plottingFrequency = 50;

    private int removedCount = 0;
    private int addedCount = 0;


    /**
     * Constructor
     *
     * @param problem The problem to be solved
     */
    public ElmasAlgorithm(Problem problem) {
        super(problem);
    }


    private List<Island> islands = new ArrayList<Island>();
    private List<Island> eliteIslands = new ArrayList<Island>();

    Random random = new Random();

    @Override
    public SolutionSet execute() throws JMException, ClassNotFoundException {

        problem = getProblem();
        mutation.setMutationsNumber(mutationNumber);


        //initialize agents
        for (int i = 0; i < islandsNumber; ++i) {
            Island individualAgents = new Island();
            islands.add(individualAgents);
            for (int j = 0; j < initialAgentsNumber; ++j) {
                individualAgents.add(createRandomAgent());
            }
        }
        for (int i = 0; i < eliteIslandsNumber; ++i) {
            eliteIslands.add(new Island());
        }


        //Agents meetings
        System.out.println(">>>>> BEFORE: " + Iterables.size(Iterables.concat(islands)));
        System.out.println(">>>>> BEFORE ENERGY: " + currentEnvironmentEnergy());
        for (int i = 0; i < iterationsNumber; ++i) {
            removedCount = 0;
            addedCount = 0;
            int islandNum = 0;
            for (Island island : islands) {
                runMeetingsOnIsland(Integer.toString(islandNum++), island, false);
            }
            for (Island eliteIsland : eliteIslands) {
                runMeetingsOnIsland("elite" + islandNum++, eliteIsland, true);
            }


            log(i);
            plot(i);

        }

        System.out.println("finished elmas. creating solution set.");
        Iterable<IndividualAgent> normalAgents = Iterables.concat(islands);
        Iterable<IndividualAgent> eliteAgents = Iterables.concat(this.eliteIslands);
        System.out.println(">>>>> AFTER ENERGY: " + currentEnvironmentEnergy());
        System.out.println(">>>>> AFTER: " + Iterables.size(normalAgents));
        System.out.println(">>>>> ELITE: " + Iterables.size(eliteAgents));


        //write solution
        final SolutionSet solutionSet = new SolutionSet();
        solutionSet.setCapacity(Integer.MAX_VALUE);

        for (Island island : getAllIslands()) {
            for (final IndividualAgent agent : island) {
                final Solution solution = new Solution(problem, agent.getWrappedArrayReal());
                problem.evaluate(solution);
                solutionSet.add(solution);
            }
        }


        return solutionSet;
    }

    private long currentEnvironmentEnergy() {
        Iterable<IndividualAgent> normalAgents = Iterables.concat(islands);
        Iterable<IndividualAgent> eliteAgents = Iterables.concat(this.eliteIslands);
        Iterable<IndividualAgent> allAgents = Iterables.concat(normalAgents, eliteAgents);
        long energy = 0;
        for (IndividualAgent agent : allAgents) {
            energy += agent.energy();
        }
        return energy;
    }

    private void log(int i) {

        if (i % 50 == 0) {
            System.out.println("iteration = " + i);
            System.out.println("removed: " + removedCount);
            System.out.println("added: " + addedCount);
        }
    }

    protected void runMeetingsOnIsland(String islandId, Island island, boolean isElite) throws JMException {

        ArrayList<IndividualAgent> islandCopy = island.copyAgentsList();
        for (IndividualAgent agent : islandCopy) {
            encounterAction(islandCopy, agent, island);
        }

        if (!isElite) {
            migrateEliteAgents(island);
        }

        //mutateCongestedAgents(island);
        if (VERBOSE) {
            System.out.println("island num = " + islandId);
            System.out.println("island size = " + island.size());
        }
    }

    private void mutateCongestedAgents(List<IndividualAgent> island) {
        for (IndividualAgent agent : island) {
            if (agent.getCongestedNeighbors() > CONGESTED_NEIGHBORS_LIMIT) {
                mutation.mutateSolution(agent.getArrayReal().array_);
            }
        }
    }

    protected IndividualAgent createRandomAgent() throws JMException {
        IndividualAgent newAgent = new IndividualAgent(problem.getNumberOfVariables(), random, problem);
        newAgent.changeEnergy(VIRGIN_BIRTH_ENERGY);
        return newAgent;
    }

    private List<Island> getAllIslands() {
        List<Island> allIslands = new LinkedList<Island>();
        allIslands.addAll(islands);
        allIslands.addAll(eliteIslands);
        return allIslands;
    }

    private void migrateEliteAgents(Island island) throws JMException {
        Iterator<IndividualAgent> agentIterator = island.iterator();
        while (agentIterator.hasNext()) {
            IndividualAgent eliteAgent = agentIterator.next();
            if (eliteAgent.energy() > ELITISM_PREDICATE &&
                    eliteAgent.getCongestedNeighbors() > CONGESTED_NEIGHBORS_LIMIT) {
                agentIterator.remove();
                eliteAgent.setElite(true);
                eliteAgent.clearCongestedNeighbors();
                eliteIslands.get(random.nextInt(eliteIslands.size())).add(eliteAgent);
            }
        }
    }

    private void plot(int iteration) {
        if (iteration % plottingFrequency == 0) {
            new SolutionPlotter(getCurrentSolutions(islands), "iter-" + iteration).plot();
            Iterable<Solution> eliteSolutions = getCurrentSolutions(eliteIslands);
            if (!Iterables.isEmpty(eliteSolutions)) {
                new SolutionPlotter(eliteSolutions, "elite-" + iteration).plot();
            }
        }
    }

    public void encounterAction(List<IndividualAgent> islandCopy, IndividualAgent agent, Island island) {
        try {
            if (!island.contains(agent)) { //agent can be migrated to other island during iteration
                return;
            }

            if (agent.energy() > MIGRATION_PREDICATE && random.nextDouble() < migrationProb) {
                agent.changeEnergy(MIGRATION_ENERGY_CHANGE);
                List<Island> islandsGroup = getIslands(agent.isElite());
                island.sendAgentToOtherIsland(agent, islandsGroup.get(random.nextInt(islandsGroup.size())));
                return;
            }

            if (!(island.size() < 2 || islandCopy.size() < 2)) {
                int otherIndex = random.nextInt(islandCopy.size());
                IndividualAgent other = islandCopy.get(otherIndex);
                while (other.equals(agent) || !island.contains(other) || other.energy() <= DEAD_PREDICATE) {
                    otherIndex = (otherIndex + 1) % islandCopy.size();
                    other = islandCopy.get(otherIndex);
                }

                updateCongestionInfo(agent, other);


                if (reproductionPredicate(other) && reproductionPredicate(agent)) {
                    island.add(reproduction(agent, other));
                    ++addedCount;
                } else {
                    battleBetween(agent, other);
                    removeIfDead(island, agent);
                    removeIfDead(island, other);
                }
            } else if (reproductionPredicate(agent)) {
                ++addedCount;
                island.add(selfReproduction(agent));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean removeIfDead(Island island, IndividualAgent other) {
        boolean removed = false;
        if (other.energy() <= DEAD_PREDICATE) {
            island.kill(other);
            ++removedCount;
            removed = true;
        }
        return removed;
    }


    private List<Island> getIslands(boolean isElite) {
        if (isElite)
            return eliteIslands;
        else
            return islands;
    }

    private void updateCongestionInfo(IndividualAgent agent1, IndividualAgent agent2) throws JMException {
        Solution solution1 = new Solution(problem, agent1.getWrappedArrayReal());
        Solution solution2 = new Solution(problem, agent2.getWrappedArrayReal());
        problem.evaluate(solution1);
        problem.evaluate(solution2);
        if (areCongested(solution1, solution2)) {
            agent1.addCongestedNeighbor();
            agent2.addCongestedNeighbor();
        }
    }

    private IndividualAgent selfReproduction(IndividualAgent individualAgent) {
        ArrayReal copy = copyArrayReal(individualAgent);
        mutation.mutateSolution(copy.array_);
        IndividualAgent child = new IndividualAgent(copy);
        transferEnergy(individualAgent, child, CHILD_TRANSFER_ENERGY);
        return child;
    }

    private ArrayReal copyArrayReal(IndividualAgent individualAgent) {
        return (ArrayReal) individualAgent.getArrayReal().deepCopy();
    }

    private void transferEnergy(IndividualAgent from, IndividualAgent to, int amount) {
        int energyFlow = Math.min(from.energy(), amount); //cant give more than has
        from.changeEnergy(-energyFlow);
        to.changeEnergy(energyFlow);
    }

    public boolean reproductionPredicate(IndividualAgent agent) {
        return agent.energy() > REPRODUCTION_PREDICATE;
    }

    private void battleBetween(IndividualAgent agent1, IndividualAgent agent2) throws JMException {
        IndividualAgent winner = getWiner(agent1, agent2);
        if (winner == agent1) {
            transferEnergy(agent2, agent1, BATTLE_TRANSFER_ENERGY);
        } else {
            transferEnergy(agent1, agent2, BATTLE_TRANSFER_ENERGY);
        }
    }

    private IndividualAgent getWiner(IndividualAgent agent1, IndividualAgent agent2) throws JMException {
        Solution solution1 = new Solution(problem, agent1.getWrappedArrayReal());
        Solution solution2 = new Solution(problem, agent2.getWrappedArrayReal());
        problem.evaluate(solution1);
        problem.evaluate(solution2);
        if (dominates(solution1, solution2)) {
            return agent1;
        } else if (dominates(solution2, solution1)) {
            return agent2;
        } else {
            if (agent1.getCongestedNeighbors() < agent2.getCongestedNeighbors()) {
                return agent1;
            } else {
                return agent2;
            }
        }
    }

    private boolean dominates(Solution solution1, Solution solution2) {
        return solution1.getObjective(0) < solution2.getObjective(0) && solution1.getObjective(1) < solution2.getObjective(1);
    }

    private IndividualAgent reproduction(IndividualAgent individualAgent, IndividualAgent other) throws JMException {
        ArrayReal copiedAgent1 = copyArrayReal(individualAgent);
        Double[] copy1 = copiedAgent1.array_;
        Double[] copy3 = copyArrayReal(other).array_;
        recombination.recombine(copy1, copy3);
        mutation.mutateSolution(copiedAgent1.array_);
        IndividualAgent child = new IndividualAgent(copiedAgent1);
        transferEnergy(individualAgent, child, CHILD_TRANSFER_ENERGY);
        transferEnergy(other, child, CHILD_TRANSFER_ENERGY);
        return child;
    }

    private Iterable<Solution> getCurrentSolutions(List<Island> islandsGroup) {
        Iterable<IndividualAgent> allAgents = Iterables.concat(islandsGroup);
        return Iterables.transform(allAgents, new Function<IndividualAgent, Solution>() {
            @Override
            public Solution apply(IndividualAgent individualAgent) {
                Solution solution = new Solution(problem, individualAgent.getWrappedArrayReal());
                try {
                    problem.evaluate(solution);
                } catch (JMException e) {
                    throw new RuntimeException(e);
                }
                return solution;
            }
        });
    }

    private boolean areCongested(Solution solution1, Solution solution2) {
        double objective1diff = Math.abs(solution1.getObjective(1) - solution2.getObjective(1));
        double objective0diff = Math.abs(solution1.getObjective(0) - solution2.getObjective(0));
        return objective0diff < CONGESTION_LIMIT_X
                && objective1diff < CONGESTION_LIMIT_Y;
    }
}
