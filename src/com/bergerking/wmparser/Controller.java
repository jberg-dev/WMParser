package com.bergerking.wmparser;

import com.bergerking.wmparser.DataModel.DataHolder;
import com.bergerking.wmparser.DataModel.DataManagementModel;
import com.bergerking.wmparser.DataModel.DataNode;
import com.bergerking.wmparser.DataModel.DataPoint;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
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

            // Note, regex is black magic.
            Pattern pattern = Pattern.compile("^\\[(.+)\\]\\s([a-zA-Z]{3,})\\s\\[(.+)\\]$");
            Matcher matcher = pattern.matcher(line);

            if(matcher.find())
            {
                List<String> tempArr = new ArrayList<>();
                LocalTime lt = LocalTime.MIN;

                // Parse data, group 1 matches the timestamp,
                // Group 2 matches the name,
                // Group 3 matches the Data nodes, I.E., what action it is, etc.
                lt = lt.parse(matcher.group(1));
                tempArr.addAll(Arrays.asList(matcher.group(3).split(", ")));
                List<DataNode> outList = parseAllDataNodes(tempArr);

                //Create the new DataPoint, add it to the current data management model
                DataPoint newDataPoint = new DataPoint(dmm.getDateHolder(), lt, matcher.group(2), outList);
                dmm.addItem(newDataPoint);
//                System.out.println(newDataPoint.toString());
            }
            else {
                if(testing) System.out.println("Failed to parse line: " + line);
                else LOGGER.log(Level.FINER, "Failed to parse message after '[' : " + line);
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
        Parse data node strings into real data nodes.
     */
    public List<DataNode> parseAllDataNodes(List<String> l) {
        List rv = new ArrayList();

        l.stream().forEach(x -> parseDataNode(x, rv));

        return rv;
    }

    public void parseDataNode(String s, List returnVal) {

        if(s.contains("=")) {
           String[] out =  s.split("=");
           if(out.length == 2) {
               returnVal.add(new DataNode(out[0], out[1]));
           }
           else LOGGER.log(Level.WARNING, "out.length on containing = were other than 2 on line: " + s);
        }

        else if(s.contains("Action string ")) {
            if(s.length() == 14) {
                returnVal.add(new DataNode(s, "NONE"));
            }
            else {
                String actionString = s.substring(14);
                returnVal.add(new DataNode("Action string", actionString));
            }
        }
        else if(s.contains("Received action number ")) {
            String actionNumber = s.substring(23);
            returnVal.add(new DataNode("Received action number", actionNumber));
        }
        else if(s.contains("action")) {
            String[] out = s.split(" ");

            if(out.length == 2) returnVal.add(new DataNode(out[0], out[1]));
            else LOGGER.log(Level.WARNING, "out.length were other than 2 on line: " + s);

        }

        else if(s.contains("source")) {
            String[] out = s.split(" ");

            if(out.length == 2) returnVal.add(new DataNode(out[0], out[1]));
            else LOGGER.log(Level.WARNING, "out.length were other than 2 on line: " + s);

        }

        else if(s.contains("target")) {
            String[] out = s.split(" ");

            if(out.length == 2) returnVal.add(new DataNode(out[0], out[1]));
            else LOGGER.log(Level.WARNING, "out.length were other than 2 on line: " + s);

        }
        else if(s.contains("Frozen. Ignoring.")){
            returnVal.add(new DataNode(s, s));
        }
        else if(s.contains("time left ")) {
            String timeLeft = s.substring(10);
            returnVal.add(new DataNode("time left", timeLeft));
        }
        else if(s.contains("Found 0 triggers.")) {
            String number = s.substring(6, 7);
            returnVal.add(new DataNode("Found triggers", number));
        }

        else LOGGER.log(Level.WARNING, "Reached catch-all on parsing with line: " + s);
    }

    /*
        Exit routine, add any cleanup to do before quitting the application here.
     */
    @FXML
    public void Exit(){
        Stage s = (Stage) mainBorderPane.getScene().getWindow();
        s.close();
    }

    public DataManagementModel getDmm() {
        if(testing) return this.dmm;
        else return null;
    }
}