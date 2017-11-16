package com.internetwarz.basketballrush;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {


    public static String EASY_MODE = "Easy";
    public static String MEDIUM_MODE = "Medium";
    public static String HARD_MODE = "Hard";

    public static final Map<String, Integer> ATTEMPTS_IN_GAMEMODE ;
    static {
        Map<String, Integer> aMap = new HashMap<String, Integer>();
        aMap.put(EASY_MODE, 3);
        aMap.put(MEDIUM_MODE, 2);
        aMap.put(HARD_MODE, 1);

        ATTEMPTS_IN_GAMEMODE = Collections.unmodifiableMap(aMap);
    }
}
