package coffee.berg.wmparser.DataModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bergerking on 2017-05-13.
 *
 * Purpose: Store a single, parsed, line of text from the read log file.
 *
 */
public class DataPoint {

    private LocalDate date;
    private String timestamp;
    private String player;
    private ArrayList<DataNode> tokens;
    private Boolean visible;

    public DataPoint() {
        this.date = LocalDate.MIN;
        this.timestamp = LocalTime.MIN.toString();
        this.player = "DEFAULT";
        ArrayList<DataNode> al = new ArrayList();
        al.add(new DataNode());
        this.tokens = al;
        this.visible = true;

    }

    public DataPoint(LocalDate date, String timestamp, String player, ArrayList<DataNode> tokens) {
        this.date = date;
        this.timestamp = timestamp;
        this.player = player;
        this.tokens = tokens;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void toggleVisible() { this.visible = !this.visible; }

    public void addToken(DataNode n) { this.tokens.add(n); }
    public List<DataNode> getTokens() {
        return this.tokens;
    }
    public String getTimestamp() {
        return this.timestamp;
    }

    public String getPlayer() {
        return this.player;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTimestamp(LocalTime timestamp) {
        this.timestamp = timestamp.toString();
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setTokens(ArrayList<DataNode> tokens) {
        this.tokens = tokens;
    }

    public byte getAmountOfVisible() {

        byte rv = 0;

        for (DataNode n : tokens)
        {
            if(n.getVisibility()) rv++;
        }


        return rv;
    }

    @Override
    public String toString() {
        String s = "";

        s += this.date.toString() + " ";
        s += this.timestamp.toString() + " ";
        s += this.player + " ";

        for(int i = 0 ; i < this.tokens.size(); i++) {
            s += "[" + this.tokens.get(i);

            if((i + 1) < this.tokens.size()) s+= "] ";
            else s += "]";
        }

        return s;
    }


}
