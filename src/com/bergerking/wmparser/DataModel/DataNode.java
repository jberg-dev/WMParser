package com.bergerking.wmparser.DataModel;

/**
 * Created by Bergerking on 2017-05-18.
 *
 *  The parsed data nodes from the macro log file, itemized in key-value format.
 *
 */
public class DataNode {

    private String key = "";
    private String value = "";
    private boolean visible = true;


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

    public void setInvisible() { this.visible = false; };
    public void setVisible() { this.visible = true; }
    public boolean getVisibility() { return this.visible; }

    @Override
    public String toString() {

        if(this.visible)return "(" + this.key +", "+ this.value + ")";
        else return "";

    }

}
