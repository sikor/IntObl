package metal;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.nsgaII.NSGAIIAdaptive;
import jmetal.metaheuristics.paes.PAES;
import jmetal.metaheuristics.pesa2.PESA2;
import jmetal.problems.ZDT.ZDT3;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;
import metal.storage.ExperimentResult;
import metal.storage.ExperimentsDao;

import java.io.IOException;
import java.util.logging.FileHandler;


public class AlgorithmsComparison {
    public static FileHandler fileHandler_;

    private static Algorithm algorithm;
    private static Problem problem;
    private static QualityIndicator indicators;

    private static ExperimentsDao experimentsDao = new ExperimentsDao();

    public static void main(String[] args) throws
            JMException,
            SecurityException,
            IOException,
            ClassNotFoundException {

        fileHandler_ = new FileHandler("NSGAII_main.log");

        problem = new ZDT3("ArrayReal", 30);
        indicators = new QualityIndicator(problem, "emas-age/solutions/resources/ZDT/ZDT3.pf");

        runExperiments();
    }

    private static void runExperiments() {

        for (Integer iterationsNumber : ExperimentConfig.comparisonIterations) {
            EmasConfig.iterationsNumber = iterationsNumber;

            runAlgorithms(EmasConfig.initialAgentsNumber * EmasConfig.islandsNumber);
        }

    }

    private static void runAlgorithms(Integer initialAgentsNumber) {
        runExperiment(initialAgentsNumber, new ElmasAlgorithm(problem));
        runExperiment(initialAgentsNumber, new NSGAII(problem));
        runExperiment(initialAgentsNumber, new PESA2(problem));
        runExperiment(initialAgentsNumber, new PAES(problem));
        runExperiment(initialAgentsNumber, new NSGAIIAdaptive(problem));
    }

    private static void runExperiment(Integer initialAgentsNumber, Algorithm newAlgorithm) {
        algorithm = newAlgorithm;

        try {
            long initTime = System.currentTimeMillis();
            SolutionSet population = algorithm.execute();
            long estimatedTime = System.currentTimeMillis() - initTime;

            experimentsDao.save(getExperimentResult(population, estimatedTime, initialAgentsNumber));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ExperimentResult getExperimentResult(SolutionSet population, long estimatedTime, Integer initialAgentsNumber) {
        ExperimentResult experimentResult = new ExperimentResult();
        experimentResult.setEliteIslandsNumber(EmasConfig.eliteIslandsNumber);
        experimentResult.setElitism(EmasConfig.ELITISM_SWITCH);
        experimentResult.setElitismPredicate(EmasConfig.ELITISM_PREDICATE);

        experimentResult.setInitialAgentsNumber(initialAgentsNumber);
        experimentResult.setIslandsNumber(EmasConfig.islandsNumber);
        experimentResult.setIterationsNumber(EmasConfig.iterationsNumber);
        experimentResult.setMutationsNumber(EmasConfig.mutationNumber);

        experimentResult.setTime(estimatedTime);
        experimentResult.setSolutionSize(population.size());

        experimentResult.setEpsilon(indicators.getEpsilon(population));
        experimentResult.setHyperVolume(indicators.getHypervolume(population) / indicators.getTrueParetoFrontHypervolume());
        experimentResult.setIgd(indicators.getIGD(population));
        experimentResult.setGd(indicators.getGD(population));
        experimentResult.setSpread(indicators.getSpread(population));

        return experimentResult;
    }
}
