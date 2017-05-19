package com.bergerking.wmparser.DataModel;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Bergerking on 2017-05-13.
 */
public class DataManagementModelTest {

    DataManagementModel d;
    DataPoint dp;
    ArrayList al;

    @Before
    public void setUp() throws  Exception {

        al = new ArrayList();

        al.add("ACTION1");
        al.add("RESULT");
        al.add("TARGET");

        LocalDate ld = LocalDate.parse("2017-03-21");
        LocalTime lt = LocalTime.parse("15:08:45");

        d = new DataManagementModel();
        dp = new DataPoint(ld, lt.toString(), "Jberg", al);

    }

    @Test
    public void addItem_properItem() throws  Exception {
        Boolean result = d.addItem(dp);
        assertTrue(result);
    }

    @Test
    public void addItem_malformedItem() throws  Exception {
        dp = new DataPoint();
        Boolean result = d.addItem(dp);
        assertFalse(result);
    }

    @Test
    public void getDataHolderForName_properItem() throws  Exception {
        d.addItem(dp);
        Optional<DataHolder> result = d.getDataHolderForName(dp.getPlayer());
        assertTrue(result.isPresent());
    }

    @Test
    public void getDataHolderForName_malformedItem() throws  Exception {

        Optional<DataHolder> result = d.getDataHolderForName("Bert");
        assertFalse(result.isPresent());

    }

    @Test
    public void addItem_add_many() throws  Exception {
        for(int i = 0; i < 100; i++) {
            d.addItem(dp);
        }
        assertEquals(100, d.getDataHolderForName(dp.getPlayer()).get().getDataPoints().size());
    }
}