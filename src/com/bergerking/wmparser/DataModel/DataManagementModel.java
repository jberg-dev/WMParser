package com.bergerking.wmparser.DataModel;

import com.bergerking.wmparser.Controller;

import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Bergerking on 2017-05-13.
 */
public class DataManagementModel {
    private static final Logger LOGGER = Logger.getLogger(DataManagementModel.class.getName());
    private static ArrayList<DataHolder> container;



    public DataManagementModel () {
        this.container = new ArrayList<>();

        ConsoleHandler ch = new ConsoleHandler();
        LOGGER.addHandler(ch);
        ch.setLevel(Level.ALL);

        if(Controller.testing) LOGGER.setLevel(Level.FINEST);
        else LOGGER.setLevel(Level.FINE);
    }

    public boolean addItem(DataPoint d) {
        if(d != null) {

            //check so data isn't obviously malformed
            if(d.getPlayer().equals("DEFAULT")) {
                if(!Controller.testing) LOGGER.log(Level.WARNING, "Attempted to add an uninitialized DataPoint to DataManagementModel");
                return false;
            }

            //get name, look for already existing store for that name.
            String name = d.getPlayer();
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

        return false;
    }

    public Optional<DataHolder> getDataHolderForName(String name) {

        Optional<DataHolder> dh = container.stream().filter(x -> name.equals(x.getName())).findFirst();
        return dh;

    }
}
