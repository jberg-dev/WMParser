package com.bergerking.wmparser.DataModel;

/**
 * Created by Bergerking on 2017-05-21.
 */
public class PairValue implements Comparable<PairValue>{
    private int nodePlace;
    private int pointPlace;

    public PairValue(int nodePlace, int pointPlace) {
        this.nodePlace = nodePlace;
        this.pointPlace = pointPlace;
    }

    public int getNodePlace() {
        return nodePlace;
    }

    public void setNodePlace(int nodePlace) {
        this.nodePlace = nodePlace;
    }

    public int getPointPlace() {
        return pointPlace;
    }

    public void setPointPlace(int pointPlace) {
        this.pointPlace = pointPlace;
    }


    @Override
    public int compareTo(PairValue pv) {

        if(pv.getNodePlace() == this.getNodePlace()) return 0;
        else if(pv.getNodePlace() > this.getNodePlace()) return -1;
        else return 1;

    }
}
