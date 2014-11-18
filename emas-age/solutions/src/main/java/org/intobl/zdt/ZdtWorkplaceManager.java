package org.intobl.zdt;

import org.jage.agent.IAgent;
import org.jage.agent.ISimpleAgent;
import org.jage.emas.agent.DefaultIslandAgent;
import org.jage.emas.agent.IndividualAgent;
import org.jage.workplace.IsolatedSimpleWorkplace;
import org.jage.workplace.Workplace;
import org.jage.workplace.manager.DefaultWorkplaceManager;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.XYDataset;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ZdtWorkplaceManager extends DefaultWorkplaceManager {


    @Inject
    ZdtProblem problem;
    private static final String timeNow = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss").format(new Date());

    public static String getTimeNow() {
        return timeNow;
    }


    @Override
    public void onWorkplaceStop(Workplace<? extends IAgent> workplace) {
        super.onWorkplaceStop(workplace);
        IsolatedSimpleWorkplace simpleAgents = (IsolatedSimpleWorkplace) workplace;
        LinkedList<IndividualAgent> agents = getAgents(simpleAgents.getAgents());

        plotSolution(agents);
    }

    private void plotSolution(LinkedList<IndividualAgent> agents) {
        PlotHelpers.plotToFile(createPlotDataSet(agents), "final");
    }

    private XYDataset createPlotDataSet(List<IndividualAgent> agents) {
        CategoryTableXYDataset dataSet = PlotHelpers.getCategoryTableXYDataset(agents);
        problem.getSolution().addToDataSet(dataSet);
        return dataSet;
    }

    private LinkedList<IndividualAgent> getAgents(List<ISimpleAgent> agents) {
        LinkedList<IndividualAgent> individualAgents = new LinkedList<IndividualAgent>();

        for (ISimpleAgent agent : agents) {
            DefaultIslandAgent island = (DefaultIslandAgent) agent;
            for (IndividualAgent individualAgent : island.getIndividualAgents()) {
                individualAgents.add(individualAgent);
            }
        }
        return individualAgents;
    }

}
