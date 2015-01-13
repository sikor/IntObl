package metal;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class EmasConfig {
    private final static Config config = ConfigFactory.load();

    public static final boolean ELITISM_SWITCH = config.getBoolean("ELITISM_SWITCH");
    public static final int BATTLE_TRANSFER_ENERGY = config.getInt("BATTLE_TRANSFER_ENERGY");
    public static final int REPRODUCTION_PREDICATE = config.getInt("REPRODUCTION_PREDICATE"); //wazne zeby rozmnazali sie tylko najlepsi
    public static final int CHILD_TRANSFER_ENERGY = config.getInt("CHILD_TRANSFER_ENERGY");
    public static final int MIGRATION_ENERGY_CHANGE = config.getInt("MIGRATION_ENERGY_CHANGE");
    public static final int MIGRATION_PREDICATE = config.getInt("MIGRATION_PREDICATE");
    public static final int DEAD_PREDICATE = config.getInt("DEAD_PREDICATE");
    public static final int ELITISM_PREDICATE = config.getInt("ELITISM_PREDICATE");
    public static final double CONGESTION_LIMIT_X = config.getDouble("CONGESTION_LIMIT_X");
    public static final double CONGESTION_LIMIT_Y = config.getDouble("CONGESTION_LIMIT_Y");
    public static final int VIRGIN_BIRTH_ENERGY = config.getInt("VIRGIN_BIRTH_ENERGY");
    public static final boolean TAKE_ELITE_ISLANDS_TO_RESULT = config.getBoolean("TAKE_ELITE_ISLANDS_TO_RESULT");
    public static final boolean TAKE_NORMAL_ISLANDS_TO_RESULT = config.getBoolean("TAKE_NORMAL_ISLANDS_TO_RESULT");


    public static final double migrationProb = config.getDouble("migrationProb");
    public static final int mutationNumber = config.getInt("mutationNumber");
    public static final int iterationsNumber = config.getInt("iterationsNumber");
    public static final int initialAgentsNumber = config.getInt("initialAgentsNumber");
    public static final int islandsNumber = config.getInt("islandsNumber");
    public static final int eliteIslandsNumber = config.getInt("eliteIslandsNumber");
    public static final double battleProbability = config.getDouble("battleProbability");
//    public static final int eliteWonCountNumber = config.getInt("eliteWonCountNumber");

    public static final int plottingFrequency = config.getInt("plottingFrequency");
    public static final boolean plot = config.getBoolean("plot");
    public static final boolean log = config.getBoolean("log");
}
