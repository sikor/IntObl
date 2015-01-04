package metal;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import jmetal.core.*;
import jmetal.encodings.variable.ArrayReal;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.crossover.SBXCrossover;
import jmetal.operators.mutation.MutationFactory;
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


    private Problem problem;
    private Mutation mutation = new Mutation();
    private Recombination recombination = new Recombination();


    private int removedCount = 0;
    private int addedCount = 0;
    private int congestedCount = 0;
    private SBXCrossover crossover;
    private Operator metalMutation;


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

        HashMap<String, Double> parameters = new HashMap<String, Double>();
        parameters.put("probability", 0.9);
        parameters.put("distributionIndex", 20.0);
        crossover = (SBXCrossover) CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);


        parameters = new HashMap<String, Double>();
        parameters.put("probability", 1.0 / problem.getNumberOfVariables());
        parameters.put("distributionIndex", 7.0);
        metalMutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);


        mutation.setMutationsNumber(EmasConfig.mutationNumber);


        //initialize agents
        for (int i = 0; i < EmasConfig.islandsNumber; ++i) {
            Island individualAgents = new Island();
            islands.add(individualAgents);
            for (int j = 0; j < EmasConfig.initialAgentsNumber; ++j) {
                individualAgents.add(createRandomAgent());
            }
        }
        for (int i = 0; i < EmasConfig.eliteIslandsNumber; ++i) {
            eliteIslands.add(new Island());
        }


        //Agents meetings
        System.out.println(">>>>> BEFORE: " + Iterables.size(Iterables.concat(islands)));
        System.out.println(">>>>> BEFORE ENERGY: " + currentEnvironmentEnergy());
        for (int i = 0; i < EmasConfig.iterationsNumber; ++i) {
            removedCount = 0;
            addedCount = 0;
            congestedCount = 0;
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

        int nonDominatedCount = 0;
        for (final IndividualAgent agent : agentsToReturn()) {
            Solution solution = agent.getSolution();
            problem.evaluate(solution);
            solutionSet.add(solution);
            ++nonDominatedCount;
        }
        System.out.println(">>> RETURNED SIZE: " + nonDominatedCount);

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
            System.out.println("congested: " + congestedCount);
        }
    }

    protected void runMeetingsOnIsland(String islandId, Island island, boolean isElite) throws JMException {

        ArrayList<IndividualAgent> islandCopy = island.copyAgentsList();
        for (IndividualAgent agent : islandCopy) {
            if (isElite) {
                eliteEncounterAction(islandCopy, agent, island);
            } else {
                encounterAction(islandCopy, agent, island);
            }
        }

        if (EmasConfig.ELITISM_SWITCH && !isElite) {
            migrateEliteAgents(island);
        }

//        mutateCongestedAgents(island);
        if (VERBOSE) {
            System.out.println("island num = " + islandId);
            System.out.println("island size = " + island.size());
        }
    }

    private void mutate(IndividualAgent agent) throws JMException {
        mutation.mutateSolution(solutionToArray(agent.getSolution()));
//        metalMutation.execute(agent.getSolution());
//        if(random.nextBoolean()){
//            metalMutation.execute(agent.getSolution());
//        }else{
//            mutation.mutateSolution(solutionToArray(agent.getSolution()));
//        }
    }

    protected IndividualAgent createRandomAgent() throws JMException, ClassNotFoundException {
        IndividualAgent newAgent = new IndividualAgent(new Solution(problem), EmasConfig.VIRGIN_BIRTH_ENERGY);
        return newAgent;
    }

    private List<Island> getAllIslands() {
        List<Island> allIslands = new LinkedList<Island>();
        allIslands.addAll(islands);
        allIslands.addAll(eliteIslands);
        return allIslands;
    }

    private void migrateEliteAgents(Island island) throws JMException {
        Island.IslandIterator agentIterator = island.iterator();
        while (agentIterator.hasNext()) {
            IndividualAgent eliteAgent = agentIterator.next();
            if (isElite(eliteAgent)) {
                eliteAgent.setElite(true);
                agentIterator.sendAgentToOtherIsland(eliteIslands.get(random.nextInt(eliteIslands.size())));
            }
        }
    }

    private boolean isElite(IndividualAgent agent) {
        return agent.getPrestige() >= EmasConfig.ELITISM_PREDICATE
                && (double) agent.getCongestedNeighbors() > agent.getAverageCongestionOfOthers();
    }

    private void plot(int iteration) {
        if (iteration % EmasConfig.plottingFrequency == 0) {
            new SolutionPlotter(getCurrentSolutions(islands), "iter-" + iteration).plot();
            Iterable<Solution> eliteSolutions = getCurrentSolutions(eliteIslands);
            if (!Iterables.isEmpty(eliteSolutions)) {
                new SolutionPlotter(eliteSolutions, "elite-" + iteration).plot();
            }
        }
    }

    public void eliteEncounterAction(List<IndividualAgent> islandCopy, IndividualAgent agent, Island island) {
        try {
            if (!island.contains(agent)) { //agent can be removed during iteration.
                return;
            }

            IndividualAgent looser = null;

            if (!(island.size() < 2 || islandCopy.size() < 2)) {
                IndividualAgent other = findAgentToMeet(islandCopy, agent, island);

                IndividualAgent winner = getWinner(agent, other);
                if (winner != null) {
                    looser = winner == agent ? other : agent;
                    looser.giveEnergy(winner, looser.energy());
                    island.kill(looser);
                }
            }

            if (looser != agent && random.nextDouble() < EmasConfig.migrationProb) {
                List<Island> islandsGroup = getIslands(agent.isElite());
                agent.migrate(island, islandsGroup.get(random.nextInt(islandsGroup.size())), EmasConfig.MIGRATION_ENERGY_CHANGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected IndividualAgent findAgentToMeet(List<IndividualAgent> islandCopy, IndividualAgent agent, Island island) {
        int otherIndex = random.nextInt(islandCopy.size());
        IndividualAgent other = islandCopy.get(otherIndex);
        while (other.equals(agent) || !island.contains(other) || other.energy() <= EmasConfig.DEAD_PREDICATE) {
            otherIndex = (otherIndex + 1) % islandCopy.size();
            other = islandCopy.get(otherIndex);
        }
        return other;
    }

    public void encounterAction(List<IndividualAgent> islandCopy, IndividualAgent agent, Island island) {
        try {
            if (!island.contains(agent)) { //agent can be removed during iteration.
                return;
            }

            if (agent.energy() > EmasConfig.MIGRATION_PREDICATE && random.nextDouble() < EmasConfig.migrationProb) {
                List<Island> islandsGroup = getIslands(agent.isElite());
                agent.migrate(island, islandsGroup.get(random.nextInt(islandsGroup.size())), EmasConfig.MIGRATION_ENERGY_CHANGE);
                return;
            }

            if (!(island.size() < 2 || islandCopy.size() < 2)) {
                IndividualAgent other = findAgentToMeet(islandCopy, agent, island);

                informationExchange(agent, other);

                if (!(reproductionPredicate(other) && reproductionPredicate(agent)) || random.nextDouble() < EmasConfig.battleProbability) {
                    battleBetween(agent, other);
                    removeIfDead(island, agent);
                    removeIfDead(island, other);
                } else {
                    island.add(reproduction(agent, other)[0]);
                    ++addedCount;
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
        if (other.energy() <= EmasConfig.DEAD_PREDICATE) {
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

    private void informationExchange(IndividualAgent agent1, IndividualAgent agent2) throws JMException {
        Solution solution1 = agent1.getSolution();
        Solution solution2 = agent2.getSolution();
        problem.evaluate(solution1);
        problem.evaluate(solution2);
        if (areCongested(solution1, solution2)) {
            agent1.addCongestedNeighbor();
            agent2.addCongestedNeighbor();
            ++congestedCount;
        }

        agent1.meet(agent2);
        agent2.meet(agent1);
    }

    private IndividualAgent selfReproduction(IndividualAgent individualAgent) throws JMException {
        Solution copy = individualAgent.copySolution();
//        metalMutation.execute(copy);
//        mutation.mutateSolution(solutionToArray(copy));
        IndividualAgent child = new IndividualAgent(copy, 0);
        mutate(child);
        transferEnergy(individualAgent, child, EmasConfig.CHILD_TRANSFER_ENERGY);
        return child;
    }

    private Double[] solutionToArray(Solution solution) {
        return ((ArrayReal) (solution.getDecisionVariables()[0])).array_;
    }

    private void transferEnergy(IndividualAgent from, IndividualAgent to, int amount) {
        int energyFlow = Math.min(from.energy(), amount); //cant give more than has
        from.giveEnergy(to, energyFlow);
    }

    public boolean reproductionPredicate(IndividualAgent agent) {
        return agent.energy() > EmasConfig.REPRODUCTION_PREDICATE;
    }

    private void battleBetween(IndividualAgent agent1, IndividualAgent agent2) throws JMException {
        IndividualAgent winner = getWinner(agent1, agent2);
        if (winner == agent1) {
            transferEnergy(agent2, agent1, EmasConfig.BATTLE_TRANSFER_ENERGY);
        } else if (winner == agent2) {
            transferEnergy(agent1, agent2, EmasConfig.BATTLE_TRANSFER_ENERGY);
        }
    }

    private IndividualAgent getWinner(IndividualAgent agent1, IndividualAgent agent2) throws JMException {
        Solution solution1 = agent1.getSolution();
        Solution solution2 = agent2.getSolution();
        problem.evaluate(solution1);
        problem.evaluate(solution2);
        if (dominates(solution1, solution2)) {
            agent1.won();
            return agent1;
        } else if (dominates(solution2, solution1)) {
            agent2.won();
            return agent2;
        } else {
//            if (agent1.getCongestedNeighbors() < agent2.getCongestedNeighbors()) {
//                return agent1;
//            } else {
//                return agent2;
//            }
//              if (random.nextDouble() < 0.5) {
//                    return agent1;
//                } else {
//                    return agent2;
//                }
            return null;
        }
    }

    private boolean dominates(Solution solution1, Solution solution2) {
        return solution1.getObjective(0) < solution2.getObjective(0) && solution1.getObjective(1) < solution2.getObjective(1);
    }

    private IndividualAgent[] reproduction(IndividualAgent individualAgent, IndividualAgent other) throws JMException {
        Solution[] offspring = (Solution[]) crossover.execute(new Solution[]{individualAgent.copySolution(), other.copySolution()});
//        metalMutation.execute(offspring[0]);
//        mutation.mutateSolution(solutionToArray(offspring[0]));

        IndividualAgent child = new IndividualAgent(offspring[0], 0);
        mutate(child);
        transferEnergy(individualAgent, child, EmasConfig.CHILD_TRANSFER_ENERGY);
        transferEnergy(other, child, EmasConfig.CHILD_TRANSFER_ENERGY);
        return new IndividualAgent[]{child};
    }

    private Iterable<Solution> getCurrentSolutions(List<Island> islandsGroup) {
        Iterable<IndividualAgent> allAgents = Iterables.concat(islandsGroup);
        return Iterables.transform(allAgents, new Function<IndividualAgent, Solution>() {
            @Override
            public Solution apply(IndividualAgent individualAgent) {
                Solution solution = individualAgent.getSolution();
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
        return objective0diff < EmasConfig.CONGESTION_LIMIT_X
                && objective1diff < EmasConfig.CONGESTION_LIMIT_Y;
    }

    /**
     * @return Nondominated agents from configured islands.
     */
    private Iterable<IndividualAgent> agentsToReturn() {
        Iterable<IndividualAgent> agents_ = Collections.emptyList();

        if (EmasConfig.TAKE_NORMAL_ISLANDS_TO_RESULt) {
            agents_ = Iterables.concat(agents_, Iterables.concat(islands));
        }
        if (EmasConfig.TAKE_ELITE_ISLANDS_TO_RESULT) {
            agents_ = Iterables.concat(agents_, Iterables.concat(eliteIslands));
        }
        final Iterable<IndividualAgent> agents = agents_;


        return Iterables.filter(agents, new Predicate<IndividualAgent>() {
            @Override
            public boolean apply(final IndividualAgent agent) {
                return Iterables.all(agents, new Predicate<IndividualAgent>() {
                    @Override
                    public boolean apply(IndividualAgent other) {
                        try {
                            return getWinner(agent, other) != other;
                        } catch (JMException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }
}

