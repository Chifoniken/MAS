package com.diploma;

/**
 * Created by arsen on 12.01.2016.
 */
public class Helper {
    private static Helper ourInstance = new Helper();

    public static Helper instance() {
        return ourInstance;
    }

    private Helper() {
    }
}
