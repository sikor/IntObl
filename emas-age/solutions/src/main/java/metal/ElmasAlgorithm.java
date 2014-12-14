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

    public static final int BATTLE_TRANSFER_ENERGY = 30;
    public static final int REPRODUCTION_PREDICATE = 90;
    public static final int CHILD_TRANSFER_ENERGY = 50;
    public static final int MIGRATION_PENALTY = -1;
    public static final int DEAD_PREDICATE = 0;
    private Problem problem;
    private Mutation mutation = new Mutation();
    private Recombination recombination = new Recombination();
    private double migrationProb = 0.1;
    private int mutationNumber = 2;
    private int iterationsNumber = 500;
    private int initialAgentsNumber = 300;
    private int islandsNumber = 10;

    private int plottingFrequency = 10;

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
    Random random = new Random();

    @Override
    public SolutionSet execute() throws JMException, ClassNotFoundException {

        Random random = new Random();

        problem = getProblem();
        mutation.setMutationsNumber(mutationNumber);


        //initialize agents
        for (int i = 0; i < islandsNumber; ++i) {
            ArrayList<IndividualAgent> individualAgents = new ArrayList<IndividualAgent>();
            islands.add(individualAgents);
            for (int j = 0; j < initialAgentsNumber; ++j) {
                IndividualAgent newAgent = new IndividualAgent(problem.getNumberOfVariables(), random, problem);
                newAgent.changeEnergy(100);
                individualAgents.add(newAgent);
            }
        }


        //Agents meetings
        for (int i = 0; i < iterationsNumber; ++i) {
            int islandNum = 0;
            for (List<IndividualAgent> island : islands) {

                agentsToRemove.clear();
                agentsToAdd.clear();

                for (int agent = 0; agent < island.size(); ++agent) {
                    encounterAction(island, agent);
                }

                Iterator<Integer> integerIterator = agentsToRemove.descendingIterator();
                while (integerIterator.hasNext()) {
                    int toRemoveIndex = integerIterator.next(); //important unboxing
                    island.remove(toRemoveIndex);
                }
                island.addAll(agentsToAdd);
                System.out.println("island num = " + islandNum++);
                System.out.println("island size = " + island.size());
                System.out.println("to add size = " + agentsToAdd.size());
                System.out.println("to remove size = " + agentsToRemove.size());
            }
            System.out.println("iteration = " + i);
            plot(i);

        }

        System.out.println("finished elmas. creating solution set.");


        //write solution
        final SolutionSet solutionSet = new SolutionSet();
        solutionSet.setCapacity(Integer.MAX_VALUE);
        for (List<IndividualAgent> island : islands) {
            for (final IndividualAgent agent : island) {
                final Solution solution = new Solution(problem, agent.getWrappedArrayReal());
                problem.evaluate(solution);
                solutionSet.add(solution);
            }
        }


        return solutionSet;
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
}
