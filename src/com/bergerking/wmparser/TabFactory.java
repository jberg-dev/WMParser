package com.bergerking.wmparser;

import com.bergerking.wmparser.DataModel.DataHolder;
import com.bergerking.wmparser.DataModel.DataPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Bergerking on 2017-05-14.
 */
public class TabFactory {

    public TabFactory() {

    }

    public Optional<Tab> manufactureTab(DataHolder datters) {
        Optional<Tab> rv = Optional.empty();
        Tab tabby = new Tab();

        Node main = null;

        try {
            main = FXMLLoader.load(getClass().getClassLoader().getResource("GenericTab.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tabby.setContent(main);
        datters.calculateTimes();
        tabby.setId(datters.getName());
        tabby.setText(datters.getName());

        Node n = tabby.getContent();

        Node rollingLog = n.lookup("#RollingLog");
        Node listofActions = n.lookup("#ListOfActions");
        Node graph = n.lookup("#Graph");

        TreeItem<String> rootNode = new TreeItem("Actions");
        rootNode.setExpanded(true);
        TreeView<String> treeView = new TreeView<>(rootNode);
        treeView.setEditable(true);
        treeView.setShowRoot(false);

        BarChart bc = (BarChart) graph;
        TableView tv = (TableView) rollingLog;
        StackPane lv = (StackPane) listofActions;

        HashMap<String, Integer> hm = (HashMap) datters.getUniqueDataNodesAndCount(true, false);
        TreeMap<String, Integer> tree = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        tree.putAll(hm);

        ArrayList<String> al = new ArrayList<>();
        tree.forEach((x, y) -> al.add(x + ": " + y));


        treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());

        for(String s : al) {
            CheckBoxTreeItem<String> childNode = new CheckBoxTreeItem<>(s);
            childNode.setSelected(true);
            rootNode.getChildren().add(childNode);
        }

        HashMap<String, Integer> tempHash = (HashMap) datters.getUniqueDataNodesAndCount(false, true);
        TreeMap<String, Integer> tempMap = new TreeMap<>(String .CASE_INSENSITIVE_ORDER);
        ArrayList<String> tempArrayList = new ArrayList();
        tempMap.putAll(tempHash);
        tempMap.forEach((x, y) -> tempArrayList.add(x + ": " + y));

        for(String s : tempArrayList) {
            boolean found = false;
            for(TreeItem<String> node : rootNode.getChildren()) {

                String[] matchThis = node.getValue().split(": ");
                if(matchThis.length == 2)
                {

                    if(s.contains(matchThis[0])) {
                        CheckBoxTreeItem<String> newLeaf = new CheckBoxTreeItem(s);
                        newLeaf.setSelected(true);
                        node.getChildren().add(newLeaf);
                        found = true;
                    }
                }
                else System.out.println("not match this! "+ node.getValue());


            }
            if(!found && Controller.testing) System.out.println("Couldn't find container for: "+ s);
        }


        lv.getChildren().add(treeView);



        TableColumn dateColumn = new TableColumn("Date");
        TableColumn timeColumn = new TableColumn("Time");
        TableColumn dataColumn = new TableColumn("Data");

        dateColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("timestamp"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("tokens"));

        tv.setItems(FXCollections.observableArrayList(datters.getDataPoints()));
        tv.setEditable(true);
        tv.getColumns().addAll(dateColumn, timeColumn, dataColumn);

        //Barchart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        bc.setTitle("Summary");
        bc.getXAxis().setAutoRanging(true);

        bc.getData().addAll(datters.getSeriesOfTimes());
        bc.getXAxis().setAutoRanging(true);
        bc.getYAxis().setAutoRanging(true);


        rv = Optional.of(tabby);
        return rv;
    }
}
