package com.bergerking.wmparser.DataModel;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Bergerking on 2017-05-13.
 */
public class DataManagementModelTest {

    DataManagementModel d;
    DataPoint dp;
    ArrayList al;

    @Before
    public void setUp() {

        al = new ArrayList();

        al.add("ACTION1");
        al.add("RESULT");
        al.add("TARGET");

        d = new DataManagementModel();
        dp = new DataPoint("2017-03-21", "15:08:45", "Jberg", al);

    }

    @Test
    public void addItem() {
        Boolean result = d.addItem(dp);
        assertTrue(result);
    }
}