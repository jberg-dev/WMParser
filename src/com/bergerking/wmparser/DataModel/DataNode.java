package com.bergerking.wmparser.DataModel;

/**
 * Created by Bergerking on 2017-05-18.
 */
public class DataNode {

    private String key = "";
    private String value = "";


    public DataNode() {
        this.key = "DEFAULT";
        this.value = "DEFAULT";
    }

    public DataNode(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.key +", "+ this.value;
    }

}
