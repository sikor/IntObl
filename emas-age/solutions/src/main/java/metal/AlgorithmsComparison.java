package metal;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.experiments.settings.*;
import jmetal.problems.ZDT.ZDT3;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;
import metal.evaluation.LiveEvaluator;
import metal.storage.ExperimentResult;
import metal.storage.ExperimentsDao;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;


public class AlgorithmsComparison {
    public static final String PROBLEM_NAME = "ZDT3";
    public static FileHandler fileHandler_;

    private static Algorithm algorithm;
    private static Problem problem;
    private static QualityIndicator indicators;

    private static int experimentNo;

    private static ExperimentsDao experimentsDao = new ExperimentsDao();

    public static ExperimentsDao dao() {
        return experimentsDao;
    }

    public static void main(String[] args) throws
            JMException,
            SecurityException,
            IOException,
            ClassNotFoundException {

        fileHandler_ = new FileHandler("NSGAII_main.log");

        problem = new ZDT3("ArrayReal", 30);
        indicators = new QualityIndicator(problem, "emas-age/solutions/resources/ZDT/ZDT3.pf");
        experimentNo = experimentsDao.generateNewExperimentNo();
        System.out.println("ExperimentNo: " + experimentNo);

        runExperiments();
    }

    private static void runExperiments() throws JMException {

        for (Integer iterationsNumber : ExperimentConfig.comparisonIterations) {
            EmasConfig.iterationsNumber = iterationsNumber;

            runAlgorithms(EmasConfig.initialAgentsNumber * EmasConfig.islandsNumber);
        }

    }

    private static void runAlgorithms(Integer initialAgentsNumber) throws JMException {
        Properties properties = new Properties();
        properties.setProperty("maxEvaluations", String.valueOf(EmasConfig.iterationsNumber));
        properties.setProperty("maxIterations", String.valueOf(EmasConfig.iterationsNumber));

        runExperiment(initialAgentsNumber, new ElmasAlgorithm(problem), "emas");
        runExperiment(initialAgentsNumber, new NSGAII_Settings(PROBLEM_NAME).configure(properties), "NSGAII");
        runExperiment(initialAgentsNumber, new SPEA2_Settings(PROBLEM_NAME).configure(properties), "SPEA2");
        runExperiment(initialAgentsNumber, new PAES_Settings(PROBLEM_NAME).configure(properties), "PAES");
        runExperiment(initialAgentsNumber, new NSGAIIAdaptive_Settings(PROBLEM_NAME).configure(properties), "NSGAIIAdaptive");
        runExperiment(initialAgentsNumber, new GDE3_Settings(PROBLEM_NAME).configure(properties), "GDE3");
    }

    private static void runExperiment(Integer initialAgentsNumber, Algorithm newAlgorithm, String algorithmName) {
        algorithm = newAlgorithm;

        try {
            long initTime = System.currentTimeMillis();
            LiveEvaluator.startEvaluation(algorithmName);
            SolutionSet population = algorithm.execute();
            LiveEvaluator.stopEvaluation();
            long estimatedTime = System.currentTimeMillis() - initTime;

//            experimentsDao.save(getExperimentResult(population, estimatedTime, initialAgentsNumber, algorithmName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ExperimentResult getExperimentResult(SolutionSet population, long estimatedTime, Integer initialAgentsNumber, String algorithmName) {
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

        experimentResult.setAlgorithm(algorithmName);
        experimentResult.setExperimentNo(experimentNo);

        return experimentResult;
    }
}
