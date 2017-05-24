package com.bergerking.wmparser.DataModel;

import com.bergerking.wmparser.Controller;
import com.sun.istack.internal.Nullable;
import javafx.scene.chart.XYChart;

import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Created by Bergerking on 2017-05-13.
 *
 *  Management system for a full set of parsed lines, the container so to speak.
 *
 */
public class DataHolder {
    private String name;
    private ArrayList<DataPoint> dp;
    private HashMap<String, TreeSet<PairValue>> metaMap;
    private long lastAdded = 0L;
    private long lastCalculated = 0L;

    public DataHolder(String name) {
        this.name = name;
        this.dp = new ArrayList<>();
        metaMap = new HashMap<String, TreeSet<PairValue>>();
    }

    public boolean addDataPoint(DataPoint d) {
        boolean rv = dp.add(d);

        if(rv != false) updateMetaMap(d);
        else Logger.getGlobal().log(Level.WARNING, "Failed to add d: " + d.toString());


        return rv;
    }

    public void updateMetaMap(DataPoint d) {

        int index = dp.indexOf(d);
        List<DataNode> l = d.getTokens();
        int iterate = 0;

        for(DataNode data : l){
            TreeSet<PairValue> pv = metaMap.get(data.getKey());


            if(pv == null){
                pv = new TreeSet<>();
                pv.add(new PairValue(index, iterate));
                metaMap.put(data.getKey(), pv);
            }
            else pv.add(new PairValue(index, iterate));


            iterate++;
        }
    }

    public void updateMetaMap(DataNode data, int indexOfDataPoint, int indexOfDataNode) {
        TreeSet<PairValue> pv = metaMap.get(data.getKey());


        if(pv == null){
            pv = new TreeSet<>();
            pv.add(new PairValue(indexOfDataPoint, indexOfDataNode));
            metaMap.put(data.getKey(), pv);
        }
        else pv.add(new PairValue(indexOfDataPoint, indexOfDataNode));
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<DataPoint> getDataPoints() {
        return this.dp;
    }

    public void calculateTimes() {
        ArrayList<DataNode> tempArr = new ArrayList();
        LocalTime lastTime = null;

        //if we've not added anything new since we last calculated, ignore and return.
        if(this.lastCalculated > this.lastAdded){
            if(Controller.testing) Logger.getGlobal().log(Level.FINE, "Attempted to calculate on instance which has had no new additions since last calculation.");
            return;
        }

        TreeSet<PairValue> pv = metaMap.get("Received action number");
        if(pv != null) {

            for(PairValue val : pv)
            {

                DataPoint d = dp.get(val.getNodePlace());

                if(lastTime != null) {
                    LocalTime tempTime = LocalTime.parse(d.getTimestamp());
                    long seconds = lastTime.until(tempTime, SECONDS);
                    addTokenTo(val.getNodePlace(), new DataNode("Seconds since last action", Long.toString(seconds)));
                    lastTime = tempTime;
                }
                else lastTime = LocalTime.parse(d.getTimestamp());
            }


        }
        else Logger.getGlobal().log(Level.WARNING, "Tried to calculate on list which does not contain 'Received action number' ");




    }

    public void addTokenTo(int dataPointPlace, DataNode d) {

        DataPoint dataPoint = dp.get(dataPointPlace);
        updateMetaMap(d, dataPointPlace, dataPoint.getTokens().size());
        dataPoint.addToken(d);

    }

    public Map<String, Integer> getUniqueDataNodesAndCount( boolean getUniqueKey, boolean getKeyAndVal ) {
        Map<String, Integer> map = new HashMap<>();
        ArrayList<String> tempArr = new ArrayList<>();
        HashMap<String, Integer> rv = new HashMap<>();

        // get all tokens, add to temporary array
        dp.forEach(x -> x.getTokens()
                .forEach(y -> tempArr.add(y.getKey())));

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
        //concatenate strings, attempt to get that string from the map.
        //If success, you get the integer value .get(key) returns value
        //else you get null.
        //Insert into the map, either as 1 as the first, or count up integer.
        String temp = s1 + ", " + s2;
        Integer count = hm.get(temp);
        hm.put(temp, (count == null) ? 1 : count + 1);
    }

    public XYChart.Series<String, Number> getSeriesOfTimes() {
        XYChart.Series returnValue = new XYChart.Series();
        returnValue.setName(this.name);
        int maxVal = 0;
        TreeMap<Integer, Integer> listOfNumbers = new TreeMap<>();

        //for each data point, if it has seconds since last action
        for(DataPoint d : dp) {
            Optional<DataNode> o = d.getTokens().stream().filter(x -> x.getKey().equals("Seconds since last action")).findFirst();
            if(o.isPresent())
            {
                //add them to a list of numbers. if there are collisions, just count up the collision by one.
                String val =  o.get().getValue();
                int parsed = Integer.parseInt(val);
                if(parsed > maxVal) maxVal = parsed;

                Integer count = listOfNumbers.get(parsed);
                listOfNumbers.put(parsed, (count == null) ? 1 : count + 1);

            }

        }

        //int i = 2 because the 0 and 1 second actions are, generally, ignorable. not elegant, needs future improvement.
        //if there is no result of listOfNumbers.get, and the next item in line after is null too, skip item.
        //this so the bar graphs does not get completely fucked up. need more elegant solution. need future improvement.
        //else, just add the data and move on to the next one
        for(int i = 2; i <= maxVal; i++) {
            Integer find = listOfNumbers.get(i);
            if(find == null) {

                if(listOfNumbers.get(i+1) != null)
                {
                    returnValue.getData().add(new XYChart.Data(Integer.valueOf(i).toString(), Integer.valueOf(0)));
                }

            }
            else returnValue.getData().add(new XYChart.Data(Integer.valueOf(i).toString(), find));
        }

        return returnValue;
    }

}

