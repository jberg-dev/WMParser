package com.bergerking.wmparser;

import com.bergerking.wmparser.DataModel.DataHolder;
import com.bergerking.wmparser.DataModel.DataPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        datters.calculateTimesGeneric();
        tabby.setId(datters.getName());
        tabby.setText(datters.getName());

        setListOfActions(tabby, datters);
        setRollingLog(tabby, datters);
        updateBarChart(tabby, datters);

        rv = Optional.of(tabby);
        return rv;
    }

    public void setListOfActions(Tab t, DataHolder hudder) {

        Node n = t.getContent();
        Node listofActions = n.lookup("#ListOfActions");

        StackPane lv = (StackPane) listofActions;


        TreeItem<String> rootNode = new TreeItem("Actions");
        rootNode.setExpanded(true);
        TreeView<String> treeView = new TreeView<>(rootNode);
        treeView.setId("TreeView");
        treeView.setEditable(true);
        treeView.setShowRoot(false);

        HashMap<String, Integer> hm = (HashMap) hudder.getUniqueDataNodesAndCount(true, false);
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

        lv.getChildren().add(treeView);

    }

    public void setRollingLog(Tab t, DataHolder doot) {
        Node n = t.getContent();

        Node rollingLog = n.lookup("#RollingLog");
        Node listofActions = n.lookup("#ListOfActions");

        TableView tv = (TableView) rollingLog;
        StackPane lv = (StackPane) listofActions;

        Node foundNode = lv.lookup("#TreeView");

        if(foundNode == null)
        {
            Logger.getGlobal().log(Level.SEVERE, "Never update rollinglog before you have updated list of actions");
            return;
        }
        else
        {
            HashMap<String, Integer> tempHash = (HashMap) doot.getUniqueDataNodesAndCount(false, true);
            TreeMap<String, Integer> tempMap = new TreeMap<>(String .CASE_INSENSITIVE_ORDER);
            ArrayList<String> tempArrayList = new ArrayList();
            tempMap.putAll(tempHash);
            tempMap.forEach((x, y) -> tempArrayList.add(x + ": " + y));

            TreeView<String> treeView = (TreeView<String>) foundNode;
            TreeItem<String> rootNode = treeView.getRoot();

            ObservableList<TreeItem<String>> obsL = rootNode.getChildren().sorted();
            ArrayList<DataPoint> hold = doot.getDataPoints();
            ArrayList<DataPoint> toDisplay = new ArrayList<>();

            for (DataPoint d : hold)
            {
                if (d.getAmountOfVisible() == 0) continue;
                toDisplay.add(d);
            }


            for(String s : tempArrayList) {
                boolean found = false;
                for(TreeItem<String> node : obsL) {

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

            TableColumn dateColumn = new TableColumn("Date");
            TableColumn timeColumn = new TableColumn("Time");
            TableColumn dataColumn = new TableColumn("Data");

            dateColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("date"));
            timeColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("timestamp"));
            dataColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("tokens"));

            ObservableList obs = FXCollections.observableArrayList(toDisplay);

            tv.setItems(obs);
            tv.setEditable(true);
            tv.getColumns().addAll(dateColumn, timeColumn, dataColumn);
        }
    }

    public boolean updateBarChart(Tab t, DataHolder dock) {
        Node n = t.getContent();
        Node graph = n.lookup("#Graph");
        StackedBarChart bc = (StackedBarChart) graph;


        //Barchart
        bc.setTitle("Summary");
        bc.getData().clear();
        bc.setLegendVisible(true);
        bc.setCategoryGap(1);

        ArrayList<String> tempArr = dock.getUniqueActionNumbers();

        for(String s : tempArr)
        {
            bc.getData().add(dock.calculateIntervalsBetweenActions(s));
        }
//        bc.getData().addAll(dock.getSeriesOfTimes());
        bc.getXAxis().setAutoRanging(true);
        bc.getYAxis().setAutoRanging(true);


        ObservableList<XYChart.Series> xys = bc.getData();
        for(XYChart.Series<String,Number> series : xys) {
            ArrayList<XYChart.Data> removelist = new ArrayList<>();

            for(XYChart.Data<String,Number> data: series.getData()) {
                if(data.getYValue().equals(0)) removelist.add(data);
            }

            series.getData().removeAll(removelist);
        }


        return true;
    }





}