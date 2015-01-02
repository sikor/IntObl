package metal;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by pawel on 16/12/14.
 */
public class Island implements Iterable<IndividualAgent> {

    public class IslandIterator implements Iterator<IndividualAgent> {

        private final Iterator<IndividualAgent> innerIterator = agents.iterator();
        private IndividualAgent current;

        @Override
        public boolean hasNext() {
            return innerIterator.hasNext();
        }

        @Override
        public IndividualAgent next() {
            return current = innerIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Use dedicated methods to remove agent from this island.");
        }

        public void sendAgentToOtherIsland(Island island) {
            Preconditions.checkNotNull(island);
            Preconditions.checkNotNull(current);
            innerIterator.remove();
            island.add(current);
        }

        public void kill() {
            Preconditions.checkNotNull(current);
            if (current.energy() > EmasConfig.DEAD_PREDICATE) {
                throw new IllegalArgumentException("can't kill agent with so much energy! " + current.energy());
            }
            innerIterator.remove();
        }
    }

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

    public IslandIterator iterator() {
        return new IslandIterator();
    }

    public void sendAgentToOtherIsland(IndividualAgent agent, Island island) {
        if (!agents.remove(agent)) {
            throw new IllegalArgumentException("this agent is not here!");
        }
        island.add(agent);
    }

    public void kill(IndividualAgent agent) {
        if (agent.energy() > EmasConfig.DEAD_PREDICATE) {
            throw new IllegalArgumentException("can't kill agent with so much energy! " + agent.energy());
        }
        if (!agents.remove(agent)) {
            throw new IllegalArgumentException("this agent is not here!");
        }
    }

    public boolean contains(IndividualAgent agent) {
        return agents.contains(agent);
    }
}
