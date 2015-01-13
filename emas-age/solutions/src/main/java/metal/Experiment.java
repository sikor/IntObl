package metal;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.problems.ZDT.ZDT3;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;
import metal.storage.ExperimentResult;
import metal.storage.ExperimentsDao;

import java.io.IOException;
import java.util.logging.FileHandler;


public class Experiment {
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
        for (Integer elitismPredicate : ExperimentConfig.elitismPredicates) {
            for (Integer eliteIslandsNumber : ExperimentConfig.eliteIslandsNumbers) {
                for (Boolean elitismSwitch : ExperimentConfig.elitismSwitches) {
                    for (Integer initialAgentsNumber : ExperimentConfig.initialAgentsNumbers) {
                        for (Integer iterationsNumber : ExperimentConfig.iterationsNumbers) {
                            for (Integer mutationNumber : ExperimentConfig.mutationNumbers) {
                                for (Integer islandsNumber : ExperimentConfig.islandsNumbers) {
                                    EmasConfig.ELITISM_PREDICATE = elitismPredicate;
                                    EmasConfig.eliteIslandsNumber = eliteIslandsNumber;
                                    EmasConfig.ELITISM_SWITCH = elitismSwitch;
                                    EmasConfig.initialAgentsNumber = initialAgentsNumber / islandsNumber;
                                    EmasConfig.iterationsNumber = iterationsNumber;
                                    EmasConfig.mutationNumber = mutationNumber;
                                    EmasConfig.islandsNumber = islandsNumber;

                                    runExperiment(initialAgentsNumber);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void runExperiment(Integer initialAgentsNumber) {
        algorithm = new ElmasAlgorithm(problem);

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
