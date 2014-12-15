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

    public static final int BATTLE_TRANSFER_ENERGY = 20;
    public static final int REPRODUCTION_PREDICATE = 90;
    public static final int CHILD_TRANSFER_ENERGY = 20;
    public static final int MIGRATION_PENALTY = -1;
    public static final int DEAD_PREDICATE = 0;
    public static final int ELITISM_PREDICATE = 100;
    private static final int START_ENERGY = 0;
    private static final double CONGESTION_LIMIT_X = 0.1;
    private static final double CONGESTION_LIMIT_Y = 0.01;
    private static final int CONGESTED_NEIGHBORS_LIMIT = 10;

    private Problem problem;
    private Mutation mutation = new Mutation();
    private Recombination recombination = new Recombination();
    private double migrationProb = 0.05;
    private int mutationNumber = 3;
    private int iterationsNumber = 2000;
    private int initialAgentsNumber = 3000;
    private int islandsNumber = 16;

    private int plottingFrequency = 50;

    private TreeSet<Integer> agentsToRemove = new TreeSet<Integer>();
    private Set<IndividualAgent> agentsToAdd = new HashSet<IndividualAgent>();

    /**
     * Constructor
     *
     * @param problem The problem to be solved
     */
    public ElmasAlgorithm(Problem problem) {
        super(problem);
    }


    private List<List<IndividualAgent>> islands = new ArrayList<List<IndividualAgent>>();
    private List<IndividualAgent> eliteIsland = new LinkedList<IndividualAgent>();

    Random random = new Random();

    @Override
    public SolutionSet execute() throws JMException, ClassNotFoundException {

        problem = getProblem();
        mutation.setMutationsNumber(mutationNumber);


        //initialize agents
        for (int i = 0; i < islandsNumber; ++i) {
            ArrayList<IndividualAgent> individualAgents = new ArrayList<IndividualAgent>();
            islands.add(individualAgents);
            for (int j = 0; j < initialAgentsNumber; ++j) {
                individualAgents.add(createRandomAgent());
            }
        }


        //Agents meetings
        System.out.println(">>>>> BEFORE: " + Iterables.size(Iterables.concat(islands)));
        for (int i = 0; i < iterationsNumber; ++i) {
            int islandNum = 0;
            for (List<IndividualAgent> island : islands) {

                agentsToRemove.clear();
                agentsToAdd.clear();

                int size = island.size();
                for (int agent = 0; agent < size; ++agent) {
                    encounterAction(island, agent);
                }

                Iterator<Integer> integerIterator = agentsToRemove.descendingIterator();
                while (integerIterator.hasNext()) {
                    int toRemoveIndex = integerIterator.next(); //important unboxing
                    island.remove(toRemoveIndex);
                }
                migrateEliteAgents(island);
                island.addAll(agentsToAdd);
                mutateCongestedAgents(island);
                System.out.println("island num = " + islandNum++);
                System.out.println("island size = " + island.size());
                System.out.println("to add size = " + agentsToAdd.size());
                System.out.println("to remove size = " + agentsToRemove.size());
            }
            System.out.println("iteration = " + i);
            plot(i);

        }

        System.out.println("finished elmas. creating solution set.");
        System.out.println(">>>>> AFTER: " + Iterables.size(Iterables.concat(islands)));
        System.out.println(">>>>> ELITE: " + eliteIsland.size());


        //write solution
        final SolutionSet solutionSet = new SolutionSet();
        solutionSet.setCapacity(Integer.MAX_VALUE);

        for (List<IndividualAgent> island : getAllIslands()) {
            for (final IndividualAgent agent : island) {
                final Solution solution = new Solution(problem, agent.getWrappedArrayReal());
                problem.evaluate(solution);
                solutionSet.add(solution);
            }
        }


        return solutionSet;
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
        newAgent.changeEnergy(70);
        return newAgent;
    }

    private List<List<IndividualAgent>> getAllIslands() {
        List<List<IndividualAgent>> allIslands = new LinkedList<List<IndividualAgent>>();
        allIslands.addAll(islands);
        allIslands.add(eliteIsland);
        return allIslands;
    }

    private void migrateEliteAgents(List<IndividualAgent> island) throws JMException {
        Iterator<IndividualAgent> agentIterator = island.iterator();
        int removedEliteAgents = 0;
        while (agentIterator.hasNext()) {
            IndividualAgent eliteAgent = agentIterator.next();
            if (eliteAgent.energy() > ELITISM_PREDICATE &&
                    eliteAgent.getCongestedNeighbors() > CONGESTED_NEIGHBORS_LIMIT) {
                agentIterator.remove();
                eliteIsland.add(eliteAgent);
                removedEliteAgents++;
            }
        }
        /*for (int i = 0; i < removedEliteAgents; i++) {
            island.add(createRandomAgent());
        }*/
    }

    private void plot(int iteration) {
        if (iteration % plottingFrequency == 0) {
            new SolutionPlotter(getCurrentSolutions(), "iter-" + iteration).plot();
        }
    }

    public void encounterAction(List<IndividualAgent> island, int agentIndex) {
        try {
            IndividualAgent agent = island.get(agentIndex);
            if (agent.energy() < DEAD_PREDICATE) {
                agentsToRemove.add(agentIndex);
                return;
            }

            if (!(island.size() < 2)) {
                int otherIndex = random.nextInt(island.size());

                if (otherIndex == agentIndex) {
                    otherIndex = (otherIndex + 1) % island.size();
                }
                IndividualAgent other = island.get(otherIndex);
                updateCongestionInfo(agent, other);


                if (reproductionPredicate(other) && reproductionPredicate(agent)) {
                    agentsToAdd.add(reproduction(agent, other));
                } else {
                    battleBetween(agent, other);
                }
            } else if (reproductionPredicate(agent)) {
                agentsToAdd.add(selfReproduction(agent));
            }

            if (random.nextDouble() < migrationProb) {
                agent.changeEnergy(MIGRATION_PENALTY);
                agentsToRemove.add(agentIndex);
                islands.get(random.nextInt(islands.size())).add(agent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        child.changeEnergy(START_ENERGY);
        transferEnergy(individualAgent, child, CHILD_TRANSFER_ENERGY);
        return child;
    }

    private ArrayReal copyArrayReal(IndividualAgent individualAgent) {
        return (ArrayReal) individualAgent.getArrayReal().deepCopy();
    }

    private void transferEnergy(IndividualAgent individualAgent, IndividualAgent child, int i) {
        i = Math.min(individualAgent.energy(), i);
        individualAgent.changeEnergy(-i);
        child.changeEnergy(i);
    }

    public boolean reproductionPredicate(IndividualAgent other) {
        return other.energy() > REPRODUCTION_PREDICATE;
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
            if (random.nextDouble() <= 0.5) {
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

    private Iterable<Solution> getCurrentSolutions() {
        Iterable<IndividualAgent> allAgents = Iterables.concat(islands);
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
