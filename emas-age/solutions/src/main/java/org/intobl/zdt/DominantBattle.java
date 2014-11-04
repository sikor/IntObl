package org.intobl.zdt;

import org.jage.emas.agent.IndividualAgent;
import org.jage.emas.battle.Battle;
import org.jage.random.INormalizedDoubleRandomGenerator;

import javax.inject.Inject;

/**
 * Created by pawel on 04/11/14.
 */
public class DominantBattle implements Battle<IndividualAgent> {


    @Inject
    private INormalizedDoubleRandomGenerator rand;

    @Override
    public IndividualAgent fight(IndividualAgent first, IndividualAgent second) {
        SolutionWithFitness firstSolution = (SolutionWithFitness) first.getSolution();
        SolutionWithFitness secondSolution = (SolutionWithFitness) second.getSolution();
        if (firstSolution.getFitness().dominates(secondSolution.getFitness())) {
            return first;
        }
        if (secondSolution.getFitness().dominates(firstSolution.getFitness())) {
            return second;
        }

        return rand.nextDouble() <= 0.5 ? first : second;
    }
}
