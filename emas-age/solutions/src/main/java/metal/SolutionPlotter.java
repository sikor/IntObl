package metal;

import com.google.common.collect.ImmutableMap;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.problems.ZDT.ZDT1;
import jmetal.problems.ZDT.ZDT2;
import jmetal.problems.ZDT.ZDT3;
import org.intobl.zdt.SolutionsList;
import org.intobl.zdt.Zdt1Problem;
import org.intobl.zdt.Zdt2Problem;
import org.intobl.zdt.Zdt3Problem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.XYDataset;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SolutionPlotter {

    private static File dir = new File("./" + System.currentTimeMillis());

    static {
        dir.mkdirs();
    }

    private final Iterable<Solution> agents;
    private final String fileName;

    private final Map<Class, SolutionsList> solutionLists = ImmutableMap.<Class, SolutionsList>builder()
            .put(ZDT1.class, new Zdt1Problem(30).getSolution())
            .put(ZDT2.class, new Zdt2Problem(30).getSolution())
            .put(ZDT3.class, new Zdt3Problem(30).getSolution())
            .build();

    public SolutionPlotter(Iterable<Solution> agents, String fileName) {
        this.agents = agents;
        this.fileName = fileName;
    }

    public SolutionPlotter(Iterator<Solution> iterator, String fileName) {
        List<Solution> solutions = new LinkedList<Solution>();
        while (iterator.hasNext()) {
            solutions.add(iterator.next());
        }
        this.agents = solutions;
        this.fileName = fileName;
    }

    public void plot() {
        CategoryTableXYDataset dataSet = getCategoryTableXYDataset();
        solutionLists.get(getProblemClass()).addToDataSet(dataSet);
        plotToFile(dataSet);
    }

    protected Class<? extends Problem> getProblemClass() {
        return agents.iterator().next().getProblem().getClass();
    }

    public CategoryTableXYDataset getCategoryTableXYDataset() {
        CategoryTableXYDataset dataSet = new CategoryTableXYDataset();
        insertToDataset(dataSet);
        return dataSet;
    }

    public void insertToDataset(CategoryTableXYDataset dataSet) {
        for (Solution agent : agents) {
            dataSet.add(agent.getObjective(0), agent.getObjective(1), ".");
        }
    }

    public void plotToFile(XYDataset dataSet) {
        JFreeChart chart = ChartFactory.createScatterPlot("ZDT", "x", "y", dataSet, PlotOrientation.VERTICAL, false, false, false);
        try {

            File file = new File(dir.getPath() + "/" + fileName + ".jpg");
            ChartUtilities.saveChartAsJPEG(file, chart, 1000, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}