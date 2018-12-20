package coffee.berg.wmparser.DataModel;

/**
 * Created by Bergerking on 2017-05-21.
 *
 * Acts as an index in the dataPoint map of the DataHolder.
 *
 * Immutable.
 *
 */
public class PairValue implements Comparable<PairValue>{
    private short nodePlace;
    private byte pointPlace;

    public PairValue(short nodePlace, byte pointPlace) {
        this.nodePlace = nodePlace;
        this.pointPlace = pointPlace;
    }

    public short getNodePlace() {
        return nodePlace;
    }

    public byte getPointPlace() {
        return pointPlace;
    }

    @Override
    public int compareTo(PairValue pv) {

        if(pv.getNodePlace() == this.getNodePlace()) return 0;
        else if(pv.getNodePlace() > this.getNodePlace()) return -1;
        else return 1;

    }
}