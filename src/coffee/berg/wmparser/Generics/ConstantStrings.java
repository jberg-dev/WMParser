package coffee.berg.wmparser.Generics;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Bergerking on 2017-06-05.
 */
public enum ConstantStrings
{
    DEFAULT_STRING("DEFAULT"),
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
    FOUND_TRIGGERS("Found triggers");

    public final String string;
    private static final HashMap<String, ConstantStrings> index = new HashMap<>();
    private static boolean initialized = false;

    ConstantStrings(String s)
    {
        string = s;
    }

    public static Optional<ConstantStrings> get(final String s)
    {
        if(!initialized)
        {
            for (ConstantStrings cs : ConstantStrings.values())
            {
                index.put(cs.string, cs);
            }
            initialized = !initialized;
        }
        return Optional.of(index.get(s));
    }
}