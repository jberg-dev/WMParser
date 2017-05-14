package com.bergerking.wmparser;

import com.bergerking.wmparser.DataModel.DataHolder;
import com.bergerking.wmparser.DataModel.DataManagementModel;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public DataManagementModel dmm = new DataManagementModel();

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
            if(!testing) LOGGER.log(Level.FINEST, "Loading file from " + file.getAbsolutePath());
            parseInput(loadSelectedFile(file));
        }
        else LOGGER.log(Level.SEVERE,"File was NULL!");




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
        else LOGGER.log(Level.WARNING, "Failed to read anything from file or file was empty.");

        return null;
    }

    /*
        Input list of strings, get out magical list of itemization.
     */
    public void parseInput(List<String> list) {
        System.out.println(list.size());
        for(String s : list) {
            parseLine(s);
        }
    }

    private void parseLine(String line){

        if(line.charAt(0) == '[') {

            Pattern pattern = Pattern.compile("^\\[(.+)\\]\\s([a-zA-Z]{3,})\\s\\[(.+)\\]$");
            Matcher matcher = pattern.matcher(line);

            if(matcher.find())
            {
                ArrayList<String> tempArr = new ArrayList<>();
                LocalTime lt = LocalTime.MIN;

//                System.out.println(matcher.group(1));
                lt = lt.parse(matcher.group(1));
//                System.out.println(lt.toString());
                tempArr.addAll(Arrays.asList(matcher.group(3).split(", ")));

                DataPoint newDataPoint = new DataPoint(dmm.getDateHolder(), lt, matcher.group(2), tempArr);
                dmm.addItem(newDataPoint);
//                System.out.println(newDataPoint.toString());
            }
            else {
                if(testing) System.out.println("Failed to parse line: " + line);
                else LOGGER.log(Level.FINE, "Failed to parse message after '[' : " + line);
            }
        }
        else if(line.charAt(0) == 'L') {
            System.out.println(line);
            Pattern pattern = Pattern.compile("(^.*)(\\d{4}-\\d{2}-\\d{2})");
            Matcher matcher = pattern.matcher(line);
            if(matcher.find()) {
                System.out.println(matcher.group(2));
                dmm.setDateHolder(LocalDate.parse(matcher.group(2)));
            }
            else System.out.println("No Match!");
        }
        else LOGGER.log(Level.FINER, "Malformed line: " + line);
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