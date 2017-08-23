package com.github.e13mort.stf.console;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class SimpleConsoleFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        final String message = formatMessage(record);
        final String throwable = formatThrowable(record);
        return String.format("%s %s \n", message, throwable);
    }

    private String formatThrowable(LogRecord record) {
        String throwable = "";
        if (record.getThrown() != null) {
            if (record.getLevel() == Level.WARNING) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println();
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();
            } else {
                throwable = record.getThrown().getMessage();
            }
        }
        return throwable;
    }
}
