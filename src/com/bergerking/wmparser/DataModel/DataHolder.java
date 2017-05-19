package com.bergerking.wmparser.DataModel;

import java.util.ArrayList;

/**
 * Created by Bergerking on 2017-05-13.
 *
 *  Management system for a full set of parsed lines, the container so to speak.
 *
 */
public class DataHolder {
    private String name;
    private ArrayList<DataPoint> dp;

    public DataHolder(String name) {
        this.name = name;
        this.dp = new ArrayList<>();
    }

    public boolean addDataPoint(DataPoint d) {
        return dp.add(d);
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<DataPoint> getDataPoints() {
        return this.dp;
    }
}
