package com.bergerking.wmparser.DataModel;

import com.sun.istack.internal.Nullable;

import java.util.*;

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

    public Map<String, Integer> getUniqueDataNodesAndCount( boolean getUniqueKey, boolean getKeyAndVal ) {
        Map<String, Integer> map = new HashMap<>();
        ArrayList<String> tempArr = new ArrayList<>();
        HashMap<String, Integer> rv = new HashMap<>();

        // get all tokens, add to temporary array
        dp.stream().forEach(x -> x.getTokens().stream().forEach(y -> tempArr.add(y.getKey())));

        // add all tokens you got to a hashmap, count collisions in the Integer
        for (String temp : tempArr) {
            Integer count = map.get(temp);
            map.put(temp, (count == null) ? 1 : count + 1);
        }



        // if you want to keep the unique keys, add them to the returnval
        if(getUniqueKey) {
            rv.putAll(map);
        }

        // if you want the key+value pair as a string, summed up and counted, add them to the returnval
        if(getKeyAndVal){

            HashMap<String, Integer> tempMap = new HashMap<>();

            /**
             * For each key in the map, iterate over every token in the class' data point storage.
             * For every token, add to the temporary map, and count it.
             */

            map.keySet().iterator()
                    .forEachRemaining(it -> dp.stream()
                                    .forEach(x -> x.getTokens()
                                            .stream()
                                            .filter(matches -> matches.getKey().contains(it))
                                                    .forEach(y -> addToMap(y.getKey(), y.getValue(), tempMap))));

            rv.putAll(tempMap);
        }

        return rv;
    }

    private void addToMap(String s1, String s2, Map<String, Integer> hm) {
        String temp = s1 + ", " + s2;
        Integer count = hm.get(temp);
        hm.put(temp, (count == null) ? 1 : count + 1);
    }


}

