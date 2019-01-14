package tests;

import coffee.berg.wmparser.Controller;
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
    Path p;
    List testFileArray = new ArrayList();
    File testFile;

    @Before
    public void setUp() throws Exception {

        try {
            p = Paths.get(this.getClass().getResource("/Sample.txt").toURI());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //load test file to use for manipulations.
        try {
            testFile = p.toFile();
            Files.lines(p).forEach(s -> testFileArray.add(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = new Controller();
    }

}