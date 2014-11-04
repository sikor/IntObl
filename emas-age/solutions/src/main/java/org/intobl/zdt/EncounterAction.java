package org.intobl.zdt;

import com.google.common.collect.Iterables;
import org.jage.agent.AgentException;
import org.jage.emas.agent.IndividualAgent;
import org.jage.emas.battle.Battle;
import org.jage.emas.energy.EnergyTransfer;
import org.jage.emas.predicate.IPredicate;
import org.jage.emas.reproduction.AsexualReproduction;
import org.jage.emas.reproduction.SexualReproduction;
import org.jage.emas.util.ChainingAction;
import org.jage.query.AgentEnvironmentQuery;
import org.jage.random.IIntRandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;

import static org.jage.action.AgentActions.addToParent;
import static org.jage.query.ValueFilters.eq;
import static org.jage.query.ValueFilters.not;

/**
 * Created by pawel on 04/11/14.
 */
public class EncounterAction extends ChainingAction<IndividualAgent> {

    private static final Logger log = LoggerFactory.getLogger(EncounterAction.class);

    @Inject
    private IIntRandomGenerator rand;

    @Inject
    private IPredicate<IndividualAgent> reproductionPredicate;

    @Inject
    private SexualReproduction<IndividualAgent> sexualReproductionStrategy;

    @Inject
    private AsexualReproduction<IndividualAgent> asexualReproductionStrategy;

    @Inject
    private Battle<IndividualAgent> battleStrategy;

    @Inject
    private EnergyTransfer<IndividualAgent> battleEnergyTransfer;

    @Override
    public void doPerform(final IndividualAgent agent) throws AgentException {
        log.debug("Performing encounter action on {}", agent);

        final Collection<IndividualAgent> neighborhood = queryForNeighbors(agent);
        if (!neighborhood.isEmpty()) {
            final IndividualAgent other = getRandomElement(neighborhood);
            log.debug("Encounter between agents {} and {}.", agent, other);

            if (reproductionPredicate.apply(agent) && reproductionPredicate.apply(other)) {
                reproductionBetween(agent, other);
            } else {
                battleBetween(agent, other);
            }
        } else if (reproductionPredicate.apply(agent)) {
            selfReproduction(agent);
        }
    }

    private Collection<IndividualAgent> queryForNeighbors(final IndividualAgent agent) throws AgentException {
        return new AgentEnvironmentQuery<IndividualAgent, IndividualAgent>().matching(not(eq(agent))).execute(
                agent.getEnvironment());
    }

    private void reproductionBetween(final IndividualAgent agent, final IndividualAgent other) throws AgentException {
        final IndividualAgent child = sexualReproductionStrategy.reproduce(agent, other);
        log.debug("Love! Agents {} and {} gave birth to {}.", agent, other, child);
        agent.getEnvironment().submitAction(addToParent(agent, child));
    }

    private void battleBetween(final IndividualAgent agent, final IndividualAgent other) throws AgentException {
        final IndividualAgent winner = battleStrategy.fight(agent, other);
        final IndividualAgent loser = winner != agent ? agent : other;
        final double energyLost = battleEnergyTransfer.transferEnergy(loser, winner);
        log.debug("Fight! Agent {} lost {} energy to {}.", loser, energyLost, winner);
    }

    private void selfReproduction(final IndividualAgent agent) throws AgentException {
        final IndividualAgent child = asexualReproductionStrategy.reproduce(agent);
        log.debug("Love? Agent {} spontaneously gave birth to {}.", agent, child);
        agent.getEnvironment().submitAction(addToParent(agent, child));
    }

    private <T> T getRandomElement(final Collection<T> collection) {
        return Iterables.get(collection, rand.nextInt(collection.size()));
    }
}
