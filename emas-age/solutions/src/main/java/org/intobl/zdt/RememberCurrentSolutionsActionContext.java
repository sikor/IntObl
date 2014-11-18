package org.intobl.zdt;

import org.jage.action.context.AgentActionContext;
import org.jage.emas.util.ChainingContext;

/**
 * Created by pawel on 18/11/14.
 */
@AgentActionContext(RememberCurrentSolutionsActionContext.Properties.REMEMBER_CURRENT_SOLUTION)
public class RememberCurrentSolutionsActionContext extends ChainingContext {
    /**
     * StatisticsUpdateActionContext properties.
     *
     * @author AGH AgE Team
     */
    public static class Properties {
        /**
         * The action name.
         */
        public static final String REMEMBER_CURRENT_SOLUTION = "rememberCurrentSolution";
    }
}
