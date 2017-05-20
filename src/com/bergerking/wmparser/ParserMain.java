package com.bergerking.wmparser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ParserMain extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {


        Parent root = FXMLLoader.load(getClass().getResource("/WMParser.fxml"));

        primaryStage.setTitle("Wurm Macro Parser");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();


    }


    public static void main(String[] args) {

        launch(args);

    }


    public ArrayList<String> getSampleTextFile() throws Exception {
        ArrayList<String> rv = new ArrayList<>();



        return rv;
    }
}
