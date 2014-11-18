package org.intobl.zdt;

import org.jage.agent.AgentException;
import org.jage.emas.agent.DefaultIslandAgent;
import org.jage.emas.agent.IndividualAgent;
import org.jage.emas.util.ChainingAction;
import org.jfree.data.xy.CategoryTableXYDataset;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawel on 18/11/14.
 */
public class RememberCurrentSolutionsAction extends ChainingAction<DefaultIslandAgent> {


    private List<CategoryTableXYDataset> solutions = new ArrayList<CategoryTableXYDataset>();

    @Inject
    ZdtProblem problem;


    Integer stepsNumber;

    public void setStepsNumber(Integer stepsNumber) {
        this.stepsNumber = stepsNumber;
    }


    @Override
    protected synchronized void doPerform(DefaultIslandAgent target) throws AgentException {
        if (target.getStep() % stepsNumber == 0) {
            List<IndividualAgent> individualAgents = target.getIndividualAgents();
            int index = (int) (target.getStep() / stepsNumber);
            if (solutions.size() > index) {
                CategoryTableXYDataset dataset = solutions.get(index);
                PlotHelpers.insertToDataset(individualAgents, dataset);
            } else {
                if (solutions.size() > 0) {
                    CategoryTableXYDataset dataSet = solutions.get(solutions.size() - 1);
                    problem.getSolution().addToDataSet(dataSet);
                    PlotHelpers.plotToFile(dataSet, String.valueOf(solutions.size()));
                }
                CategoryTableXYDataset categoryTableXYDataset = PlotHelpers.getCategoryTableXYDataset(individualAgents);
                solutions.add(categoryTableXYDataset);
            }
            System.out.println("Remembering current solution: " + solutions.size() + " " + target.getAddress());
        }
    }


    public List<CategoryTableXYDataset> getSolutions() {
        return solutions;
    }
}
