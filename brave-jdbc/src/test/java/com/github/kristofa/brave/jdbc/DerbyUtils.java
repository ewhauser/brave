package com.github.kristofa.brave.jdbc;

import java.io.OutputStream;

public class DerbyUtils {

    //Get rid of the annoying derby.log file
    public static void disableLog() {
        System.setProperty("derby.stream.error.field", BraveJdbcTests.class.getName() + ".DEV_NULL");
    }

    public static final OutputStream DEV_NULL = new OutputStream() {
        public void write(int b) {}
    };

}
