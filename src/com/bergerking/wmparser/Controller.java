package com.bergerking.wmparser;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bergerking on 2017-04-23.
 */
public class Controller {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu File;

    @FXML
    private MenuItem Load;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    public void loadFile() {
        FileChooser fileChooser = new FileChooser();

        List<String> extensions = new LinkedList<String>();
        extensions.add("*.txt");
        extensions.add("*.log");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text files", extensions);
        fileChooser.setSelectedExtensionFilter(filter);
        fileChooser.setTitle("Select Macro Log File");
        File file = fileChooser.showOpenDialog(mainBorderPane.getScene().getWindow());
        loadSelectedFile(file);
    }

    private void loadSelectedFile(File file){

    }
}