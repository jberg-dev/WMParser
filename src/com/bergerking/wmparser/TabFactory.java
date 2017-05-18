package com.bergerking.wmparser;

import com.bergerking.wmparser.DataModel.DataHolder;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by Bergerking on 2017-05-14.
 */
public class TabFactory {

    public TabFactory() {

    }

    public Optional<Tab> manufactureTab(DataHolder datters) {
        Optional<Tab> rv = Optional.empty();
        Tab tabby = new Tab();

        try {
            String s = "GenericTab.fxml";
            File f = new File(s);
            System.out.println(f.exists());
            FXMLLoader loader = new FXMLLoader();
            tabby.setContent(loader.load(getClass().getClassLoader().getResource("GenericTab.fxml")));
            System.out.println(tabby);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        tabby.setId(datters.getName());
        tabby.setText(datters.getName());

        Node n = tabby.getContent();

        Node rollingLog = n.lookup("#RollingLog");
        Node listofActions = n.lookup("#ListOfActions");
        Node graph = n.lookup("#Graph");





        return rv;
    }
}
