package coffee.berg.wmparser.DataModel;

import coffee.berg.wmparser.ConstantStrings;

/**
 * Created by Bergerking on 2017-05-18.
 *
 *  The parsed data nodes from the macro log file, itemized in key-value format.
 *
 */
public class DataNode {

    private ConstantStrings key;
    private String value = "";
    private boolean visible = true;


    public DataNode() {
        this.key = ConstantStrings.DEFAULT_STRING;
        this.value = "DEFAULT";
    }

    public DataNode(ConstantStrings key, String value) {
        this.key = key;
        this.value = value;
    }

    public ConstantStrings getKey() {
        return key;
    }

    public void setKey(ConstantStrings key) {
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

        if(this.visible)return "(" + this.key.string +", "+ this.value + ")";
        else return "";

    }

}
