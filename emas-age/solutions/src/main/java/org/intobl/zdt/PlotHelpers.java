package org.intobl.zdt;

import org.jage.emas.agent.IndividualAgent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.XYDataset;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlotHelpers {
    public static CategoryTableXYDataset getCategoryTableXYDataset(List<IndividualAgent> agents) {
        CategoryTableXYDataset dataSet = new CategoryTableXYDataset();
        insertToDataset(agents, dataSet);
        return dataSet;
    }

    public static void insertToDataset(List<IndividualAgent> agents, CategoryTableXYDataset dataSet) {
        for (IndividualAgent agent : agents) {
            ZdtSolution solution = (ZdtSolution) agent.getSolution();
            List<Double> values = solution.getFitness().getValues();
            dataSet.add(values.get(0), values.get(1), ".");
        }
    }

    public static void plotToFile(XYDataset dataSet, String fileName) {
        JFreeChart chart = ChartFactory.createScatterPlot("ZDT", "x", "y", dataSet, PlotOrientation.VERTICAL, false, false, false);
        try {
            File dir = new File("./" + ZdtWorkplaceManager.getTimeNow());
            dir.mkdirs();
            File file = new File(dir.getPath() + "/" + fileName + ".jpg");
            ChartUtilities.saveChartAsJPEG(file, chart, 1000, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}