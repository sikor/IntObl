package metal.evaluation;

import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import metal.AlgorithmsComparison;
import metal.ElmasAlgorithm;
import metal.EmasConfig;
import metal.storage.ExperimentResult;

/**
 * Created by pawel on 20/01/15.
 */
public class LiveEvaluator {

    private static String algorithmName;
    private static long startTime = 0;
    private static long evalDelay = 0;
    private static int step = EmasConfig.databaseLogStep;
    private static int iterationNumber = 0;


    public static void startEvaluation(String name) {
        algorithmName = name;
        startTime = System.currentTimeMillis();
        iterationNumber = 0;
        evalDelay = 0;
        step = EmasConfig.databaseLogStep;
    }

    public static void onNewSolution(SolutionSet population, boolean getSubfront) {
        ++iterationNumber;
        if (iterationNumber % step == 0) {
            long evalStart = System.currentTimeMillis();

            SolutionSet notDominated;
            if (getSubfront) {
                Ranking ranking = new Ranking(population);
                notDominated = ranking.getSubfront(0);
            } else {
                notDominated = population;
            }

            storeAndUpdateDelay(evalStart, notDominated);
        }
    }

    public static void onNewSolution(SolutionSet population) {
        onNewSolution(population, true);
    }

    public static void onNewSolution(ElmasAlgorithm elmasAlgorithm) throws JMException {
        ++iterationNumber;
        if (iterationNumber % step == 0) {

            long evalStart = System.currentTimeMillis();

            SolutionSet notDominated = elmasAlgorithm.getNonDominatedSolutions();
            storeAndUpdateDelay(evalStart, notDominated);
        }
    }

    public static void storeAndUpdateDelay(long evalStart, SolutionSet notDominated) {
        long thisIterationTime = evalStart - startTime - evalDelay;
        ExperimentResult result = AlgorithmsComparison.getExperimentResult(notDominated, thisIterationTime, 0, algorithmName);
        result.setIterationsNumber(iterationNumber);
        AlgorithmsComparison.dao().save(result);

        long evalStop = System.currentTimeMillis();
        evalDelay += evalStop - evalStart;
    }


    public static void stopEvaluation() {
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("%s -> full time = %d, delay time = %d", algorithmName, endTime - startTime, evalDelay));
    }
}
