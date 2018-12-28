package coffee.berg.wmparser.DataModel;

import coffee.berg.wmparser.ConstantStrings;
import coffee.berg.wmparser.Controller;
import coffee.berg.wmparser.GenericTabController;
import javafx.scene.chart.XYChart;

import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Created by Bergerking on 2017-05-13.
 *
 *  Management system for a full set of parsed lines, the container so to speak.
 *
 */
public class DataHolder {

    private static final Logger logger = Logger.getLogger(DataHolder.class.getName());

    private String name;
    private ArrayList<DataPoint> dp;
    private HashMap<ConstantStrings, ArrayList<PairValue>> metaMap;
    private HashMap<String, ArrayList<PairValue>> ignoreList;

    public DataHolder(String name) {
        this.name = name;
        this.dp = new ArrayList<>();
        this.metaMap = new HashMap<>();
        this.ignoreList = new HashMap<>();
    }

    /*
        Adding DataPoints this way is to build a meta map of what fixed string tokens can
        be found where, which lets us forgo a constant iteration over all the points.

        The application is intended to easily and constantly be able to change what parts of
        the data is taken into consideration when calculating the graphs, so this should,
        in theory, save quite a bit of computation time.
     */
    public boolean addDataPoint(DataPoint d)
    {
        byte iterate = 0;
        for (DataNode temp : d.getTokens())
        {
            ArrayList<PairValue> indexArr = metaMap.get(temp.getKey());
            if (indexArr == null)
            {
                ArrayList<PairValue> init = new ArrayList<>();
                init.add(new PairValue((short) dp.size(), iterate++));
                metaMap.put(temp.getKey(), init);
            }
            else
            {
                indexArr.add(new PairValue((short) dp.size(), iterate++));
            }
        }
        return dp.add(d);
    }
    /*
        If you ever get to the point of needing to add an extra token to a DataPoint, you have
        to make sure that it gets properly indexed too.
     */
    private void addExtraToken(DataPoint _d, DataNode _node)
    {
        // We do not want to have multiple same-string tokens on a single DP.
        // If you want to re-calculate and update the time string, make a new method lazy.
        if (checkSoDataPointDoesNotHaveToken(_d, _node.getKey()))
        {
            if (Controller.testing)
                logger.fine("Skipping adding extra token " + _node.getKey() + " to " + _d.toString());
            return;
        }

        ArrayList<PairValue> indexArr = metaMap.get(_node.getKey());

        if (indexArr == null)
        {
            ArrayList<PairValue> init = new ArrayList<>();
            init.add(new PairValue((short) dp.size(), (byte) _d.getTokens().size()));
            metaMap.put(_node.getKey(), init);
        }
        else
        {
            indexArr.add(new PairValue((short) dp.size(), (byte) _d.getTokens().size()));
        }

        _d.getTokens().add(_node);
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<DataPoint> getDataPoints() {
        return this.dp;
    }

    private boolean checkSoDataPointDoesNotHaveToken(DataPoint _d, ConstantStrings _cs)
    {
        for (DataNode dn : _d.getTokens())
        {
            if (dn.getKey() == _cs)
                return true;
        }

        return false;
    }

    public void calculateTimesGeneric () {
        LocalTime lastTime = null;

        // get all tokens from the meta map index
        ArrayList<PairValue> index = metaMap.get(ConstantStrings.RECIEVED_ACTION_NUMBER);

        for (int i = 0; i < index.size(); i++)
        {
            DataPoint d = dp.get(index.get(i).getNodePlace());

            if (d == null)
            {
                logger.severe("the datapoint d is null. i=" + i);
                continue;
            }

            if(lastTime != null) {
                LocalTime tempTime = LocalTime.parse(d.getTimestamp());
                long seconds = lastTime.until(tempTime, SECONDS);

                DataNode tempo = new DataNode(ConstantStrings.NAME_OF_MACRO_TIME_STRING, Long.toString(seconds));
                addExtraToken(d, tempo);

                lastTime = tempTime;
            }
            else
            {
                DataNode tempo = new DataNode(ConstantStrings.NAME_OF_MACRO_TIME_STRING, "0");
                addExtraToken(d, tempo);

                lastTime = LocalTime.parse(d.getTimestamp());
            }
        }

//        System.out.println("Failed to calculate "+ iterateFail + " times since the line was irrelevant. Succeeded with "+ iterateSucceed +" actions.");
    }

    public ArrayList<String> getUniqueActionNumbers()
    {
        ArrayList<String> rv = new ArrayList<>();
        HashSet<String> tempSet = new HashSet<>();
        ArrayList<PairValue> matchSet = metaMap.get(ConstantStrings.RECIEVED_ACTION_NUMBER);

        if (matchSet != null)
        {
            for (PairValue pv : matchSet)
            {
                tempSet.add(dp.get(pv.getNodePlace()).getTokens().get(pv.getPointPlace()).getValue());
            }

            rv.addAll(tempSet);

        }
        else Logger.getGlobal().warning("Could not find any holder for recieved action number, cancelling.");



        return rv;
    }

    /**
     * Basically:
     * Get the array list of pair values from the meta map cache for that constant.
     * Once you have all the values, iterate over them and calculate how long time it was
     * between the actions being sent from the client from the last time it was sent.
     *
     * if @_sameActionNumbersOnly is false, calculate timer between all actions sent
     * irregardless if they're the same action number or not.
     *
     */
    public ArrayList<XYChart.Data<String, Number>> calculateIntervalsBetweenActions(String _actionNumber,
                                                                           final boolean _sameActionNumbersOnly)
    {
        ArrayList<XYChart.Data<String, Number>> returnValue = new ArrayList<>();
        LocalTime lastTime = null;
        TreeMap<Integer, Integer> listOfNumbers = new TreeMap<Integer, Integer>();
        int maxVal = 0;

        ArrayList<PairValue> recievedActionNumberStrings = metaMap.get(ConstantStrings.RECIEVED_ACTION_NUMBER);

        if (recievedActionNumberStrings != null)
        {

            for( PairValue pValue : recievedActionNumberStrings)
            {
                DataPoint currentPoint = dp.get(pValue.getNodePlace());
                String s = currentPoint.getTokens().get(pValue.getPointPlace()).getValue();

                // if any node in the current point is not desired to be seen,
                // the entire node is made invisible.
                if(!currentPoint.isVisible())
                    continue;

                if (!s.equals(_actionNumber) && _sameActionNumbersOnly)
                    continue;

                if(lastTime != null)
                {
                    LocalTime tempTime = LocalTime.parse(dp.get(pValue.getNodePlace()).getTimestamp());
                    int seconds = (int) lastTime.until(tempTime, SECONDS);
                    if(seconds > maxVal) maxVal = seconds;

                    Integer count = listOfNumbers.get(seconds);
                    listOfNumbers.put(seconds, (count == null) ? 1 : count + 1);
                    lastTime = tempTime;


                }
                else
                    lastTime = LocalTime.parse(dp.get(pValue.getNodePlace()).getTimestamp());

            }
            //todo add filter so the user can choose what to ignore and not.
            for(int i = 0; i <= maxVal; i++) {
                Integer find = listOfNumbers.get(i);
                if(find != null)
                    returnValue.add(new XYChart.Data<>("" + i, find));
            }

        }
        else
            Logger.getGlobal().warning("Could not find meta map for Recieved action numer, aborting");

        return returnValue;
    }


    public Map<String, Integer> getUniqueDataNodesAndCount( boolean getUniqueKey, boolean getKeyAndVal ) {
        Map<String, Integer> map = new HashMap<>();
        ArrayList<ConstantStrings> tempArr = new ArrayList<>();
        HashMap<String, Integer> rv = new HashMap<>();

        // get all tokens, add to temporary array
        dp.forEach(x -> x.getTokens()
                .forEach(y -> tempArr.add(y.getKey())));

        // add all tokens you got to a hashmap, count collisions in the Integer
        for (ConstantStrings temp : tempArr) {
            Integer count = map.get(temp);
            map.put(temp.string, (count == null) ? 1 : count + 1);
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
                                            .filter(matches -> matches.getKey().string.contains(it))
                                                    .forEach(y -> addToMap(y.getKey().string, y.getValue(), tempMap))));

            rv.putAll(tempMap);
        }

        return rv;
    }

    private void addToMap(String s1, String s2, Map<String, Integer> hm) {
        String temp = s1 + ", " + s2;
        Integer count = hm.get(temp);
        hm.put(temp, (count == null) ? 1 : count + 1);
    }

    public XYChart.Series<Number, Number> getSeriesOfTimes() {
        XYChart.Series returnValue = new XYChart.Series();
        returnValue.setName(this.name);
        int maxVal = 0;
        TreeMap<Integer, Integer> listOfNumbers = new TreeMap<>();


        for(DataPoint d : dp) {
            Optional<DataNode> o = d.getTokens().stream().filter(x -> x.getKey().equals(ConstantStrings.NAME_OF_MACRO_TIME_STRING)).findFirst();
            if(o.isPresent())
            {
                String val =  o.get().getValue();
                int parsed = Integer.parseInt(val);
                if(parsed > maxVal) maxVal = parsed;

                Integer count = listOfNumbers.get(parsed);
                listOfNumbers.put(parsed, (count == null) ? 1 : count + 1);

            }

        }

        for(int i = 2; i <= maxVal; i++) {
            Integer find = listOfNumbers.get(i);
            if(find == null) {

//                returnValue.getData().add(new XYChart.Data(Integer.valueOf(i).toString(), Integer.valueOf(0)));


                returnValue.getData().add(new XYChart.Data(Integer.valueOf(i), Integer.valueOf(0)));


            }
            else returnValue.getData().add(new XYChart.Data(Integer.valueOf(i), find));
        }

        return returnValue;
    }

}

