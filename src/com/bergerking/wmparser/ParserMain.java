package com.bergerking.wmparser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.*;

public class ParserMain extends Application {

    private static final Logger LOGGER = Logger.getLogger(ParserMain.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/WMParser.fxml"));


        Parent root = loader.load();
        Scene mainStage = new Scene(root, 800, 500);
        ((Controller) loader.getController()).setScene(mainStage);




        URL path;

        Tab mainTab = new Tab();
        Node main = null;
        loader = new FXMLLoader();

        path = getClass().getResource("/MainTab.fxml");
        try {
            loader.setLocation(path);
            main = loader.load();
        } catch (IOException e){
            System.out.println("Not found: " + path);
            e.printStackTrace();
        }


        primaryStage.setTitle("Wurm Macro Parser [ALPHA 0.01]");
        primaryStage.setScene(mainStage);
        primaryStage.show();

        mainTab.setContent(main);
        mainTab.setText("[Main Tab]");
        mainTab.setId("Main");
        mainTab.setClosable(false);

        Node n = null;

        if(mainTab != null) n = mainTab.getContent().lookup("#LogRoll");

        if(n != null)
        {
            class Console extends OutputStream {
                private TextArea console;

                public Console(TextArea console) {
                    this.console = console;
                }

                public void appendText(String valueOf) {
                    Platform.runLater(() -> console.appendText(valueOf));
                }

                public void write(int b) throws IOException {
                    appendText(String.valueOf((char)b));
                }

            }

            Logger globalLogger = Logger.getGlobal();
            Handler[] handlers = globalLogger.getHandlers();
            for(Handler handler : handlers) {
                globalLogger.removeHandler(handler);
            }

            TextArea console = (TextArea) n;
            PrintStream ps = new PrintStream(new Console(console));
            System.setOut(ps);
            System.setErr(ps);
            SimpleFormatter fmt = new SimpleFormatter();
            StreamHandler sh = new StreamHandler(System.out, fmt);
            LOGGER.addHandler(sh);
            sh.setLevel(Level.ALL);


            Node mtp = root.lookup("#mainTabPane");
            TabPane holdPane = (TabPane) mtp;
            holdPane.getTabs().add(mainTab);

            LOGGER.log(Level.INFO, "Logger Initialized");


        }
        else {
            LOGGER.log(Level.WARNING, "Could not find tag #LogRoll in MainTabPane");
        }




    }


    public static void main(String[] args) {

        launch(args);

    }


    public ArrayList<String> getSampleTextFile() throws Exception {
        ArrayList<String> rv = new ArrayList<>();



        return rv;
    }
}
