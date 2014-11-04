package org.intobl.zdt;

import org.jage.emas.agent.IndividualAgent;
import org.jage.emas.battle.Battle;

/**
 * Created by pawel on 04/11/14.
 */
public class DominantBattle implements Battle<IndividualAgent> {
    @Override
    public IndividualAgent fight(IndividualAgent first, IndividualAgent second) {
        first.getSolution();
        return null;
    }
}
