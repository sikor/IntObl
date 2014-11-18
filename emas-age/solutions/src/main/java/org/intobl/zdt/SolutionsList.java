package org.intobl.zdt;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.jfree.data.xy.CategoryTableXYDataset;

/**
 * Created by pawel on 18/11/14.
 */
public class SolutionsList {

    private final ImmutableList<Double> x;
    private final ImmutableList<Double> y;
    private final CategoryTableXYDataset dataSet = new CategoryTableXYDataset();
    private final String name;


    public SolutionsList(ImmutableList<Double> x, ImmutableList<Double> y, String name) {
        Preconditions.checkArgument(x.size() == y.size());
        this.name = name;
        this.x = ImmutableList.copyOf(x);
        this.y = ImmutableList.copyOf(y);
        for (int i = 0; i < x.size(); ++i) {
            dataSet.add(x.get(i), y.get(i), name);
        }
    }

    public ImmutableList<Double> getX() {
        return x;
    }

    public ImmutableList<Double> getY() {
        return y;
    }


    public CategoryTableXYDataset getDataSet() {
        return dataSet; //oh, oh so much immutable
    }

    public void addToDataSet(CategoryTableXYDataset dataSet) {
        for (int i = 0; i < x.size(); ++i) {
            dataSet.add(x.get(i), y.get(i), name);
        }
    }

    public String getName() {
        return name;
    }
}
