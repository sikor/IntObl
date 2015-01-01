package metal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by pawel on 16/12/14.
 */
public class Island implements Iterable<IndividualAgent> {

    private HashSet<IndividualAgent> agents = new HashSet<IndividualAgent>();

    public void add(IndividualAgent randomAgent) {
        agents.add(randomAgent);
    }

    public int size() {
        return agents.size();
    }

    public ArrayList<IndividualAgent> copyAgentsList() {
        return new ArrayList<IndividualAgent>(agents);
    }

    public Iterator<IndividualAgent> iterator() {
        return agents.iterator();
    }

    public void sendAgentToOtherIsland(IndividualAgent agent, Island island) {
        if (!agents.remove(agent)) {
            throw new IllegalArgumentException("this agent is not here!");
        }
        island.add(agent);
    }

    public void kill(IndividualAgent agent) {
        if (agent.energy() > EmasConfig.DEAD_PREDICATE && !agent.isElite()) {
            throw new IllegalArgumentException("can't kill agent with so much energy!");
        }
        if (!agents.remove(agent)) {
            throw new IllegalArgumentException("this agent is not here!");
        }
    }

    public boolean contains(IndividualAgent agent) {
        return agents.contains(agent);
    }
}
