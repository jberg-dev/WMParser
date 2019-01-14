package coffee.berg.wmparser.DataModel;

import coffee.berg.wmparser.Controller;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Bergerking on 2017-05-13.
 *
 *  Container of the containers of all the parsed players' logs.
 *
 */
public class DataManagementModel {

    private static final Logger LOGGER = Logger.getLogger(DataManagementModel.class.getName());
    public ArrayList<DataHolder> container;
    private LocalDate holder = LocalDate.MIN;



    public DataManagementModel () {
        this.container = new ArrayList<>();

        ConsoleHandler ch = new ConsoleHandler();
        LOGGER.addHandler(ch);
        ch.setLevel(Level.ALL);

        if(Controller.testing) LOGGER.setLevel(Level.FINEST);
        else LOGGER.setLevel(Level.FINE);
    }

    public boolean addItem(DataPoint d) {
        if (d != null) {

            //check so data isn't obviously malformed
            if (d.getPlayer().equals("DEFAULT")) {
                if(!Controller.testing)
                    LOGGER.log(Level.WARNING, "Attempted to add an uninitialized DataPoint to DataManagementModel");
                return false;
            }

            //get name, look for already existing store for that name.
            final String name = d.getPlayer();
            Optional<DataHolder> dh = container.stream().filter(x -> name.equals(x.getName())).findFirst();

            //if present, add to existing
            if (dh.isPresent()) {
                dh.get().addDataPoint(d);

               return true;
            }

            //else make new one
            else {
                DataHolder newDataHolder = new DataHolder(name);
                newDataHolder.addDataPoint(d);

                this.container.add(newDataHolder);

                return true;
            }
        }
        LOGGER.log(Level.WARNING, "This message should never be reached, you have failed to add a data point");
        return false;
    }

    public Optional<DataHolder> getDataHolderForName(String name) {

        Optional<DataHolder> dh = container.stream().filter(x -> name.equals(x.getName())).findFirst();
        return dh;

    }

    public void setDateHolder(LocalDate ld) {
        this.holder = ld;
    }

    public LocalDate getDateHolder() {
        return this.holder;
    }

    public Optional<ArrayList<String>> getAllHolders()
    {
        Optional<ArrayList<String>> rv = Optional.empty();
        ArrayList<String> als = new ArrayList<>();

        container.stream().forEach(x -> als.add(x.getName()));

        if(als.size() > 0) rv = Optional.of(als);

        return rv;
    }
}
