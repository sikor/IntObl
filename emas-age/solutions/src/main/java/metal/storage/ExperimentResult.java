package metal.storage;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ExperimentResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private long time;
    private double hyperVolume;
    private double igd;
    private double gd;
    private double spread;
    private double epsilon;
    private int solutionSize;

    private int elitismPredicate;
    private boolean elitism;
    private int mutationsNumber;
    private int iterationsNumber;
    private int initialAgentsNumber;
    private int islandsNumber;
    private int eliteIslandsNumber;
    private String algorithm;

    private int experimentNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getHyperVolume() {
        return hyperVolume;
    }

    public void setHyperVolume(double hyperVolume) {
        this.hyperVolume = hyperVolume;
    }

    public double getIgd() {
        return igd;
    }

    public void setIgd(double igd) {
        this.igd = igd;
    }

    public double getSpread() {
        return spread;
    }

    public void setSpread(double spread) {
        this.spread = spread;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public int getElitismPredicate() {
        return elitismPredicate;
    }

    public void setElitismPredicate(int elitismPredicate) {
        this.elitismPredicate = elitismPredicate;
    }

    public boolean isElitism() {
        return elitism;
    }

    public void setElitism(boolean elitism) {
        this.elitism = elitism;
    }

    public int getMutationsNumber() {
        return mutationsNumber;
    }

    public void setMutationsNumber(int mutationsNumber) {
        this.mutationsNumber = mutationsNumber;
    }

    public int getIterationsNumber() {
        return iterationsNumber;
    }

    public void setIterationsNumber(int iterationsNumber) {
        this.iterationsNumber = iterationsNumber;
    }

    public int getInitialAgentsNumber() {
        return initialAgentsNumber;
    }

    public void setInitialAgentsNumber(int initialAgentsNumber) {
        this.initialAgentsNumber = initialAgentsNumber;
    }

    public int getIslandsNumber() {
        return islandsNumber;
    }

    public void setIslandsNumber(int islandsNumber) {
        this.islandsNumber = islandsNumber;
    }

    public int getEliteIslandsNumber() {
        return eliteIslandsNumber;
    }

    public void setEliteIslandsNumber(int eliteIslandsNumber) {
        this.eliteIslandsNumber = eliteIslandsNumber;
    }

    public double getGd() {
        return gd;
    }

    public void setGd(double gd) {
        this.gd = gd;
    }

    public int getSolutionSize() {
        return solutionSize;
    }

    public void setSolutionSize(int solutionSize) {
        this.solutionSize = solutionSize;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getExperimentNo() {
        return experimentNo;
    }

    public void setExperimentNo(int experimentNo) {
        this.experimentNo = experimentNo;
    }
}
