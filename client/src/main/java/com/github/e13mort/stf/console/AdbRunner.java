package com.github.e13mort.stf.console;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdbRunner {
    private static final String ADB_DIRECTORY = "platform-tools";
    private final String androidSdkPath;
    private static final String TAG_ADB = "ADB";
    private final Logger logger;

    AdbRunner(String androidSdkPath, Logger logger) {
        if (androidSdkPath == null) {
            androidSdkPath = "";
        }
        this.androidSdkPath = androidSdkPath;
        this.logger = logger;
    }

    public void connectToDevice(String connectionUrl) throws IOException {
        runComplexCommand("adb", "connect", connectionUrl);
        runComplexCommand("adb", "wait-for-device");
    }

    private void runComplexCommand(String... params) throws IOException {
        File adb = new File(androidSdkPath + File.separator + ADB_DIRECTORY);
        Process exec = new ProcessBuilder(params)
                .directory(adb)
                .start();
        runProcess(exec);
    }

    private void runProcess(Process process) throws IOException {
        final InputStream stream = process.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                log(TAG_ADB, line);
            }
        } catch (IOException e) {
            log(e);
        } finally {
            reader.close();
            stream.close();
        }
    }

    private void log(Exception e) {
        logger.log(Level.INFO, "message {0}", e.getMessage());
    }

    private void log(String tag, String message) {
        logger.info(tag + ": " + message);
    }
}
