package com.bergerking.wmparser;

import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Bergerking on 2017-05-13.
 */
public class ControllerTest {

    Controller controller;
    final String testFileName = "Sample.txt";
    Path p = Paths.get(testFileName);
    List testFileArray = new ArrayList();
    File testFile;

    @Before
    public void setUp() throws Exception {

        //load test file to use for manipulations.
        try {
            testFile = new File(testFileName);
            Files.lines(p).forEach(s -> testFileArray.add(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = new Controller();
    }

    @Test
    public void loadSelectedFile_predetermined_file() throws Exception {
        List list = controller.loadSelectedFile(testFile);
        assertEquals(1247, list.size());

    }

    @Test
    public void loadSelectedFile_expect_error() throws  Exception {
        assertNull(controller.loadSelectedFile(null));
    }

    @Test
    public void parseInput() throws Exception {
        controller.parseInput(testFileArray);
        assertTrue(true);
    }

    @Test
    public void tabFactory() throws Exception {
        //need to initialize a JFXPanel for this test to even work. do nothing with it.
        JFXPanel p = new JFXPanel();


        controller.parseInput(testFileArray);
        TabFactory tf = new TabFactory();
        tf.manufactureTab(controller.getDmm().getDataHolderForName("Valerie").get());

    }

}