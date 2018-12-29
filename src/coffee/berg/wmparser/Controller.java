package coffee.berg.wmparser;

import coffee.berg.wmparser.DataModel.DataManagementModel;
import coffee.berg.wmparser.DataModel.DataNode;
import coffee.berg.wmparser.DataModel.DataPoint;
import coffee.berg.wmparser.Generics.ConstantStrings;
import coffee.berg.wmparser.Generics.Pair;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
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
    private boolean multiLineLog;
    private String DataPointSharedUUID;
    private DataManagementModel dmm = new DataManagementModel();
    private ArrayList<Pair<Tab, GenericTabController>> tabsAndControllers;
    final static Timer timer = new Timer("Keep Graphs Sorted", true);


    // Patterns so we don't compile them all the time.
    private static Pattern datePattern = Pattern.compile("(^.*)(\\d{4}-\\d{2}-\\d{2})");
    // Note, regex is black magic.
    private static Pattern linePattern = Pattern.compile("^\\[(.+)\\]\\s([a-zA-Z]{3,})\\s\\[(.+)\\]$");



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
        tabsAndControllers = new ArrayList<>();
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
                    Optional<Pair<Tab, GenericTabController>> t = tf.manufactureTab(dmm.getDataHolderForName(al.get(i)).get());

                    if(t.isPresent()){
                        mainTabPane.getTabs().add(t.get().getFirst());
                        tabsAndControllers.add(t.get());
                    }
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
                        // we don't really care about being interrupted here.
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

            Matcher matcher = linePattern.matcher(line);

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
                ArrayList<DataNode> outList = parseAllDataNodes(tempArr);

                //Create the new DataPoint, add it to the current data management model
                if (outList.size() > 0)
                {
                    DataPoint newDataPoint = new DataPoint(moo.getDateHolder(), time, matcher.group(2), outList);
                    moo.addItem(newDataPoint);
                }
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
            Matcher matcher = datePattern.matcher(line);
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

        for (String x : allDataNodes)
        {
            Optional<DataNode> got = parseDataNode(x);
            if (got.isPresent())
                rv.add(got.get());
        }

        return rv;
    }

    /**
     * TODO: Make the splitting generic instead of hardcoded!
     *
     * @param s The raw line logged to the log file
     * @return
     */

    private Optional<DataNode> parseDataNode(String s) {

        if (s.contains("="))
        {
           String[] out = s.split("=");
           if (out.length == 2)
           {
               if (out[0].contains(ConstantStrings.STARTING.string))
                    return Optional.of(new DataNode(ConstantStrings.STARTING, out[1]));
           }
           else
               LOGGER.log(Level.WARNING, "out.length on containing = were other than 2 on line: " + s);
        }

        else if (s.contains("Action string "))
        {
            if (s.length() == 14)
            {
                return Optional.of(new DataNode(ConstantStrings.NATURAL_END_OF_ACTION, "Natural end of action"));
            }
            else
            {
                String actionString = s.substring(14);
                return Optional.of(new DataNode(ConstantStrings.ACTION_STRING, actionString));
            }
        }
        else if (s.contains("Received action number "))
        {
            String actionNumber = s.substring(23);
            return Optional.of(new DataNode(ConstantStrings.RECIEVED_ACTION_NUMBER, actionNumber));
        }
        else if (s.contains("action"))
        {
            String[] out = s.split(" ");

            if (out.length == 2)
                return Optional.of(new DataNode(ConstantStrings.ACTION_NUMBER, out[1]));
            else
                LOGGER.log(Level.WARNING, "out.length were other than 2 on line: " + s);

        }

        else if (s.contains("source")) {
            String[] out = s.split(" ");

            if (out.length == 2)
                return Optional.of(new DataNode(ConstantStrings.SOURCE, out[1]));
            else
                LOGGER.log(Level.WARNING, "out.length were other than 2 on line: " + s);

        }

        else if (s.contains("target"))
        {
            String[] out = s.split(" ");

            if (out.length == 2)
                return Optional.of(new DataNode(ConstantStrings.TARGET, out[1]));
            else
                LOGGER.log(Level.WARNING, "out.length were other than 2 on line: " + s);

        }
        else if (s.contains("Frozen. Ignoring."))
        {
            return Optional.of(new DataNode(ConstantStrings.FROZEN_IGNORING, s));
        }
        else if (s.contains("time left "))
        {
            String timeLeft = s.substring(10);
            return Optional.of(new DataNode(ConstantStrings.TIME_LEFT, timeLeft));
        }
        else if(s.contains("Found 0 triggers.")) {
            String number = s.substring(6, 7);
            DataNode dn = new DataNode(ConstantStrings.FOUND_TRIGGERS, number);
//            dn.setInvisible();
            return Optional.of(dn);
        }
//        else if(s.contains("macroquestion timeout. ")) {
//            String quack = s.substring(24);
//            String[] out = quack.split(": ");
//
//            if(out.length == 2) {
//                returnVal.add(new DataNode("Macroquestion", "timeout"));
//                returnVal.add(new DataNode(out[0], out[1]));
//            }
//            else LOGGER.log(Level.WARNING, "out.length were other than 2 on line: " + s);
//        }
//        else if(s.contains("wrong")){
//            String[] out = s.split(": ");
//
//            if(out.length == 2) returnVal.add(new DataNode(out[0], out[1]));
//            else LOGGER.log(Level.WARNING, "out.length were other than 2 on line: " + s);
//        }
//        else if(s.contains("out of ")) {
//            String amount = s.substring(7, 8);
//            returnVal.add(new DataNode("Out of", amount));
//        }

        else LOGGER.log(Level.WARNING, "Reached catch-all on parsing with line: " + s);
        return Optional.empty();
    }

    /*
        Exit routine, add any cleanup to do before quitting the application here.
     */
    @FXML
    public void Exit(){
        timer.cancel();
        System.exit(0);
    }

    public DataManagementModel getDmm() {
        if(testing) return this.dmm;
        else return null;
    }

    /*
        Save current graph as an image. You choose where.
     */
    @FXML
    public void graphAsImage()
    {
        StackedBarChart bc = (StackedBarChart) mainTabPane.getSelectionModel().getSelectedItem().getContent().lookup("#Graph");
        if(bc != null)
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select destination");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setInitialFileName(mainTabPane.getSelectionModel().getSelectedItem().getId() +
            "_"+System.currentTimeMillis()+".png");
            File selected = fileChooser.showSaveDialog(mainBorderPane.getScene().getWindow());
            if(selected != null)
            {
                WritableImage snapshot = bc.snapshot(null, null);
                try
                {
                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", selected);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
                LOGGER.info("No file selected, doing nothing.");

        }
        else
            if (testing)
                LOGGER.severe("Not on a tab with a graph. Doing nothing.");
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