package metal;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;

public class ExperimentConfig {
    private final static Config config = ConfigFactory.load();

    public static final List<Integer> elitismPredicates = config.getIntList("experiment.ELITISM_PREDICATE");
    public static final List<Boolean> elitismSwitches = config.getBooleanList("experiment.ELITISM_SWITCH");
    public static final List<Integer> mutationNumbers = config.getIntList("experiment.mutationNumber");
    public static final List<Integer> iterationsNumbers = config.getIntList("experiment.iterationsNumber");
    public static final List<Integer> initialAgentsNumbers = config.getIntList("experiment.initialAgentsNumber");
    public static final List<Integer> eliteIslandsNumbers = config.getIntList("experiment.eliteIslandsNumber");
    public static final List<Integer> islandsNumbers = config.getIntList("experiment.islandsNumber");

    public static final List<Integer> comparisonIterations = config.getIntList("experiment.comparison.iterationsNumber");
}
