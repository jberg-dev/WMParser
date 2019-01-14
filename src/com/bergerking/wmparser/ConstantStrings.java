package com.bergerking.wmparser;

/**
 * Created by Bergerking on 2017-06-05.
 */
public enum ConstantStrings
{
    DEFAULT_STRING("DEFAULT)"),
    ACTION_STRING("Action string"),
    NATURAL_END_OF_ACTION("Natural end of action"),
    RECIEVED_ACTION_NUMBER("Received action number"),
    ACTION_NUMBER("actionNumber"),
    STARTING("starting"),
    SOURCE("source"),
    TARGET("target"),
    FROZEN_IGNORING("Frozen. Ignoring."),
    TIME_LEFT("time left"),
    NAME_OF_MACRO_TIME_STRING("Seconds since last action"),

    VERSION_OF_INDEX("1.0");

    public final String string;

    ConstantStrings(String s)
    {
        string = s;
    }
}
