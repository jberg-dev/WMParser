package com.bergerking.wmparser;

import com.bergerking.wmparser.DataModel.DataPoint;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    public static final boolean testing = true;

    public void initialize(){
        ConsoleHandler ch = new ConsoleHandler();
        LOGGER.addHandler(ch);
        ch.setLevel(Level.ALL);

        if(testing) LOGGER.setLevel(Level.FINEST);
        else LOGGER.setLevel(Level.FINE);

        //System.out.println(System.getProperty("user.dir"));

//        try {
//            Path p = Paths.get("easySample.txt");
//            List testFileEasy = new ArrayList();
//            Files.lines(p).forEach(s -> testFileEasy.add(s));
//
//            testFileEasy.forEach(s -> System.out.println(s));
//            System.out.println(testFileEasy.size());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    /*
        Generic file selection function.
     */
    @FXML
    public void loadFile() {
        //Make file chooser
        FileChooser fileChooser = new FileChooser();

        //Filters
        List<String> extensions = new LinkedList<String>();
        extensions.add("*.txt");
        extensions.add("*.log");

        //Display and get file chosen, pass on to function who reads the file.
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text files", extensions);
        fileChooser.setSelectedExtensionFilter(filter);
        fileChooser.setTitle("Select Macro Log File");
        File file = fileChooser.showOpenDialog(mainBorderPane.getScene().getWindow());

        //Handle the file
        if(file != null) {
            LOGGER.log(Level.FINEST, "Loading file from " + file.getAbsolutePath());
            parseInput(loadSelectedFile(file));
        }
        else LOGGER.log(Level.SEVERE,"Controller.loadFile: File was NULL!");

    }
    /*
        Read file passed in, return set of lines.
     */
    public List<String> loadSelectedFile(File file){

        List<String> input = new ArrayList<>();

        try {
            if(file != null) Files.lines(file.toPath()).forEach(s -> input.add(s));
        }
        catch (IOException e){
            LOGGER.log(Level.FINE, e.toString());
        }


        if(input.size() >= 1) {
            if(testing) System.out.println("Read "+ input.size() + " lines from file");
            return input;
        }
        else LOGGER.log(Level.WARNING, "Controller.loadSelectedFile: Failed to read anything from file or file was empty.");

        return null;
    }

    //TODO
    private List<DataPoint> parseInput(List list)
    {
        return null;
    }

    /*
        Exit routine, add any cleanup to do before quitting the application here.
     */
    @FXML
    public void Exit(){
        Stage s = (Stage) mainBorderPane.getScene().getWindow();
        s.close();
    }
}