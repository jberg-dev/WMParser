package com.bergerking.wmparser;

import com.bergerking.wmparser.DataModel.DataManagementModel;
import com.bergerking.wmparser.DataModel.DataNode;
import com.bergerking.wmparser.DataModel.DataPoint;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
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

    @FXML
    private Label statusLabel;

    private StringProperty stringProperty;
    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    public static boolean testing;
    private DataManagementModel dmm = new DataManagementModel();

    public void initialize(){

        //Load properties file
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "config.properties";
            input = Controller.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                System.out.println("Sorry, unable to find " + filename);
                return;
            }

            prop.load(input);
            testing = Boolean.parseBoolean(prop.getProperty("testing"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(input != null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        bindStatusLabel();

    }


    /*
        Generic file selection function.
     */
    @FXML
    public void loadFile(){
        loadFile(null);
    }

    public void bindStatusLabel() {
        stringProperty = new SimpleStringProperty("Welcome to WMacroParser");
        statusLabel.textProperty().bind(stringProperty);
    }

    public void loadFile(File file) {

        if(file == null) {
            //Make file chooser
            FileChooser fileChooser = new FileChooser();

            //Display and get file chosen, pass on to function who reads the file.
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.log"));
            fileChooser.setTitle("Select Macro Log File");
            file = fileChooser.showOpenDialog(mainBorderPane.getScene().getWindow());

            //Handle the file and add to view
        }

        if(file != null) {
            if(!testing) LOGGER.log(Level.INFO, "Loading file from " + file.getAbsolutePath());
            parseInput(loadSelectedFile(file));
            addNewTab();

        }
        else LOGGER.log(Level.SEVERE,"File was NULL!");

    }
    /*
        Read file passed in, return set of lines.
     */
    private List<String> loadSelectedFile(File file){

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
        Add tab to window.
     */
    private void addNewTab() {
        Optional<ArrayList<String>> o = dmm.getAllHolders();
        TabFactory tf = new TabFactory();

        if(o.isPresent()) {
            ArrayList<String> al= o.get();

            for(int i = 0; i < al.size(); i++) {

                final int iterate = i;

                if(!mainTabPane.getTabs().stream().anyMatch(x -> x.getId().equals(al.get(iterate)))) {
                    Optional<Tab> t = tf.manufactureTab(dmm.getDataHolderForName(al.get(i)).get());

                    if(t.isPresent()) mainTabPane.getTabs().add(t.get());
                    else LOGGER.log(Level.WARNING, "failed to produce tab for" + al.get(iterate));
                }
                else System.out.println("Did not attempt to create new tab for: " + al.get(i));

            }
        }
        else LOGGER.log(Level.FINE, "Could not get a list of holders from DMM - Optional not present!");
    }


    /*
        Input list of strings, get out magical list of itemization.
    */
    private void parseInput(List<String> list) {
        DataManagementModel moo = this.dmm;

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                int i = 0;
                int size = list.size();

                for(String s : list) {
                    parseLine(s, moo);
                    i++;
                    updateMessage("Read "+ i +"/"+ size);
                    try {
                        TimeUnit.MICROSECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null ;
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> addNewTab());
                System.out.println("Done!");
                updateMessage("");
            }
        };
        task.messageProperty().addListener((obs, oldMessage, newMessage) -> stringProperty.set(newMessage));

        new Thread(task).start();




    }

    /*
        Parse a line, separate it out into all the components, add it as a datapoint.
     */
    private void parseLine(String line, DataManagementModel moo){

        if(line.charAt(0) == '[') {

            // Note, regex is black magic.
            Pattern pattern = Pattern.compile("^\\[(.+)\\]\\s([a-zA-Z]{3,})\\s\\[(.+)\\]$");
            Matcher matcher = pattern.matcher(line);

            if(matcher.find())
            {
                List<String> tempArr = new ArrayList<>();
                LocalTime lt = LocalTime.MIN;
                String time = "";

                // Parse data, group 1 matches the timestamp,
                // Group 2 matches the name,
                // Group 3 matches the Data nodes, I.E., what action it is, etc.
                time = lt.parse(matcher.group(1)).format(DateTimeFormatter.ISO_LOCAL_TIME);
                tempArr.addAll(Arrays.asList(matcher.group(3).split(", ")));
                List<DataNode> outList = parseAllDataNodes(tempArr);

                //Create the new DataPoint, add it to the current data management model
                DataPoint newDataPoint = new DataPoint(moo.getDateHolder(), time, matcher.group(2), outList);
                moo.addItem(newDataPoint);
//                System.out.println(newDataPoint.toString());
            }

//            else {
//                List<String> tempArr = new ArrayList<>();
//                LocalTime lt = LocalTime.MIN;
//
//                pattern = Pattern.compile("^\\[(.+?)\\]\\s<+([a-zA-Z]{3,})>+\\s(.+)$");
//                matcher = pattern.matcher(line);
//
//
//                if(matcher.find()) {
//                    String time;
//                    time = lt.parse(matcher.group(1)).format(DateTimeFormatter.ISO_LOCAL_TIME);                    tempArr.addAll(Arrays.asList(matcher.group(3).split(", ")));
//                    List<DataNode> outList = parseAllDataNodes(tempArr);
//
//                    DataPoint newDataPoint = new DataPoint(dmm.getDateHolder(), time, matcher.group(2), outList);
//                    dmm.addItem(newDataPoint);
//                }
            else {
                System.out.println("Failed to parse line: " + line);
            }


        }
        else if(line.charAt(0) == 'L') {
            Pattern pattern = Pattern.compile("(^.*)(\\d{4}-\\d{2}-\\d{2})");
            Matcher matcher = pattern.matcher(line);
            if(matcher.find()) {
                moo.setDateHolder(LocalDate.parse(matcher.group(2)));
            }
            else LOGGER.log(Level.INFO, "No Match for: " + line);
        }
        else LOGGER.log(Level.WARNING, "Malformed line: " + line);
    }

    /*
        Parse data node strings into real data nodes.
     */
    private ArrayList<DataNode> parseAllDataNodes(List<String> allDataNodes) {
        ArrayList<DataNode> rv = new ArrayList<>();

        allDataNodes.forEach(x -> parseDataNode(x, rv));

        return rv;
    }

    private void parseDataNode(String s, ArrayList<DataNode> returnVal) {

        if(s.contains("=")) {
           String[] out =  s.split("=");
           if(out.length == 2) {
               returnVal.add(new DataNode(out[0], out[1]));
           }
           else LOGGER.log(Level.WARNING, "out.length on containing = were other than 2 on line: " + s);
        }

        else if(s.contains("Action string ")) {
            if(s.length() == 14) {
                returnVal.add(new DataNode("Action string", "Natural end of action"));
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

            if(out.length == 2) returnVal.add(new DataNode("actionNumber", out[1]));
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
        else if(s.contains("macroquestion timeout. ")) {
            String quack = s.substring(24);
            String[] out = quack.split(": ");

            if(out.length == 2) {
                returnVal.add(new DataNode("Macroquestion", "timeout"));
                returnVal.add(new DataNode(out[0], out[1]));
            }
            else LOGGER.log(Level.WARNING, "out.length were other than 2 on line: " + s);
        }
        else if(s.contains("wrong")){
            String[] out = s.split(": ");

            if(out.length == 2) returnVal.add(new DataNode(out[0], out[1]));
            else LOGGER.log(Level.WARNING, "out.length were other than 2 on line: " + s);
        }
        else if(s.contains("out of ")) {
            String amount = s.substring(7, 8);
            returnVal.add(new DataNode("Out of", amount));
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


    /*
        Demo function, and easy test of functionality.
     */
    @FXML
    public void Test() {
        List testFileArray = new ArrayList();

        System.out.println("Reading from the sample log....");

        try {

            final Path path;


            if(!testing) {
                URI uri = Controller.class.getClass().getResource("/Sample.txt").toURI();
                final Map<String, String> env = new HashMap<>();
                final String[] array = uri.toString().split("!");
                final FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
                path = fs.getPath(array[1]);
            }
            else {
                path = Paths.get(Controller.class.getClass().getResource("/Sample.txt").toURI());
            }
            Files.lines(path).forEach(s -> testFileArray.add(s));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finished reading the sample log, it was "+ testFileArray.size() + " lines long");

        parseInput(testFileArray);


    }
}