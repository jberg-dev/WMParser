package com.bergerking.wmparser.DataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bergerking on 2017-05-13.
 *
 * Purpose: Store a single, parsed, line of text from the read log file.
 *
 */
public class DataPoint {

    private String date;
    private String timestamp;
    private String player;
    private ArrayList<String> tokens;

    public DataPoint() {
        this.date = "DEFAULT";
        this.timestamp = "DEFAULT";
        this.player = "DEFAULT";
        ArrayList al = new ArrayList();
        al.add("DEFAULT");
        this.tokens = al;

    }

    public DataPoint(String date, String timestamp, String player, ArrayList<String> tokens) {
        this.date = date;
        this.timestamp = timestamp;
        this.player = player;
        this.tokens = tokens;
    }

    public String getDate() {
        return this.date;
    }

    public ArrayList<String> getTokens() {
        return this.tokens;
    }
    public String getTimestamp() {
        return this.timestamp;
    }

    public String getPlayer() {
        return this.player;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setTokens(ArrayList<String> tokens) {
        this.tokens = tokens;
    }
    @Override
    public String toString() {
        String s = "";

        s += this.date + " ";
        s += this.timestamp + " ";
        s += this.player + " ";

        for(int i = 0 ; i < this.tokens.size(); i++) {
            s += "[" + this.tokens.get(i);

            if((i + 1) < this.tokens.size()) s+= "] ";
            else s += "}";
        }

        return s;
    }


}
