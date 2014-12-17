package metal;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import javax.inject.Inject;
import java.util.Random;

/**
 * Created by pawel on 04/11/14.
 */
public class Mutation {
    private Random random = new Random();

    private Integer mutationsNumber;

    @Inject
    public void setMutationsNumber(Integer mutationsNumber) {
        this.mutationsNumber = mutationsNumber;
    }

    private Function<Double[], Void> swap = new Function<Double[], Void>() {
        public Void apply(Double[] representation) {
            int first = random.nextInt(representation.length);
            int second = random.nextInt(representation.length);
            try {
                Double firstVal = representation[first];
                Double secondVal = representation[second];
                representation[first] = secondVal;
                representation[second] = firstVal;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    };

    private Function<Double[], Void> decreaseAll = new Function<Double[], Void>() {
        public Void apply(Double[] representation) {
            for (int i = 0; i < representation.length; i++) {
                representation[i] -= 0.05;
            }
            return null;
        }
    };

    private Function<Double[], Void> mutateFirst = new Function<Double[], Void>() {
        public Void apply(Double[] representation) {
            representation[0] = random.nextDouble();
            return null;
        }
    };

    private Function<Double[], Void> mutateOne = new Function<Double[], Void>() {
        public Void apply(Double[] representation) {
            representation[random.nextInt(representation.length)] = random.nextDouble();
            return null;
        }
    };

    private Function<Double[], Void> identity = new Function<Double[], Void>() {
        public Void apply(Double[] representation) {
            return null;
        }
    };
    ImmutableList<Function<Double[], Void>> mutateFunctions = ImmutableList.<Function<Double[], Void>>builder()
//            .add(mutateFirst)
            .add(mutateFirst)
            .add(decreaseAll)
            .add(mutateOne)
//            .add(swap)
//            .add(identity)
            .build();


    private void clearInvalidData(Double[] solution) {
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] > 1.0) {
                solution[i] = 1.0;
            } else if (solution[i] < 0.0) {
                solution[i] = 0.0;
            }
        }
    }

    public void mutateSolution(Double[] solution) {
        for (int i = 0; i < mutationsNumber; i++) {
                mutateFunctions.get(random.nextInt(mutateFunctions.size())).apply(solution);
        }

        clearInvalidData(solution);
    }
}
