package com.bergerking.wmparser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ParserMain extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {


        Parent root = FXMLLoader.load(getClass().getResource("WMParser.fxml"));

        primaryStage.setTitle("Wurm Macro Parser");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();


    }


    public static void main(String[] args) {



        launch(args);

    }

    public Node getGenericTab() throws Exception {
        return FXMLLoader.load(getClass().getResource("GenericTab.fxml"));
    }
}
