package com.bergerking.wmparser.DataModel;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by Bergerking on 2017-05-13.
 */
public class DataManagementModel {

    private static ArrayList<DataHolder> container;

    public DataManagementModel () {
        this.container = new ArrayList<>();
    }

    public boolean addItem(DataPoint d) {
        if(d != null) {

            String name = d.getPlayer();
            Optional<DataHolder> dh = container.stream().filter(x -> name.equals(x.getName())).findFirst();

            if (dh.isPresent()) {
                dh.get().addDataPoint(d);
                return true;
            }

            else {
                DataHolder newDataHolder = new DataHolder(name);
                newDataHolder.addDataPoint(d);
                return true;
            }
        }

        return false;
    }
}
