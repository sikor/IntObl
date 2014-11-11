package org.intobl.zdt;

import org.jage.agent.IAgent;
import org.jage.agent.ISimpleAgent;
import org.jage.emas.agent.DefaultIslandAgent;
import org.jage.emas.agent.IndividualAgent;
import org.jage.workplace.IsolatedSimpleWorkplace;
import org.jage.workplace.Workplace;
import org.jage.workplace.manager.DefaultWorkplaceManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.XYDataset;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ZdtWorkplaceManager extends DefaultWorkplaceManager {
    @Override
    public void onWorkplaceStop(Workplace<? extends IAgent> workplace) {
        super.onWorkplaceStop(workplace);
        IsolatedSimpleWorkplace simpleAgents = (IsolatedSimpleWorkplace) workplace;
        LinkedList<IndividualAgent> agents = getAgents(simpleAgents.getAgents());

        plotSolution(agents);
    }

    private void plotSolution(LinkedList<IndividualAgent> agents) {
        plotToFile(createPlotDataSet(agents));
    }

    private XYDataset createPlotDataSet(List<IndividualAgent> agents) {
        CategoryTableXYDataset dataSet = new CategoryTableXYDataset();
        for (IndividualAgent agent : agents) {
            ZdtSolution solution = (ZdtSolution) agent.getSolution();
            List<Double> values = solution.getFitness().getValues();
            dataSet.add(values.get(0), values.get(1), ".");
        }

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

    private void plotToFile(XYDataset dataSet) {
        JFreeChart chart = ChartFactory.createScatterPlot("ZDT", "x", "y", dataSet, PlotOrientation.VERTICAL, false, false, false);
        String timeNow = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss").format(new Date());
        try {
            ChartUtilities.saveChartAsJPEG(new File("emas_chart" + timeNow + ".jpg"), chart, 1000, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
