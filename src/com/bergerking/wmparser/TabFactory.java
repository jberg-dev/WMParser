package com.bergerking.wmparser;

import com.bergerking.wmparser.DataModel.DataHolder;
import com.bergerking.wmparser.DataModel.DataNode;
import com.bergerking.wmparser.DataModel.DataPoint;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import javax.swing.text.html.ImageView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Bergerking on 2017-05-14.
 */
public class TabFactory {

    public TabFactory() {

    }

    public Optional<Tab> manufactureTab(DataHolder datters) {
        Optional<Tab> rv = Optional.empty();
        Tab tabby = new Tab();

        //This is fucking ugly, but I spent 8+ hours trying to get it to work
        //to only find this workaround. deal with it.
        ParserMain pm = new ParserMain();

        try {
            tabby.setContent(pm.getGenericTab());
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabby.setId(datters.getName());
        tabby.setText(datters.getName());

        Node n = tabby.getContent();

        Node rollingLog = n.lookup("#RollingLog");
        Node listofActions = n.lookup("#ListOfActions");
        Node graph = n.lookup("#Graph");


        Label lab = (Label) graph;
        TableView tv = (TableView) rollingLog;
        StackPane lv = (StackPane) listofActions;

        TreeMap<String, Integer> tree = (TreeMap) datters.getUniqueDataNodesAndCount(true, true);





        TableColumn dateColumn = new TableColumn("Date");
        TableColumn timeColumn = new TableColumn("Time");
        TableColumn dataColumn = new TableColumn("Data");

        dateColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("timestamp"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("tokens"));

        tv.setItems(FXCollections.observableArrayList(datters.getDataPoints()));
        tv.getColumns().addAll(dateColumn, timeColumn, dataColumn);



        rv = Optional.of(tabby);



        return rv;
    }
}
