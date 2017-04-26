package com.github.e13mort.stf.console;

import java.io.*;

class AdbRunner {
    private static final String ADB_DIRECTORY = "platform-tools";
    private final String androidSdkPath;
    private static final String TAG_ADB = "ADB";

    AdbRunner(String androidSdkPath) {
        if (androidSdkPath == null) {
            androidSdkPath = "";
        }
        this.androidSdkPath = androidSdkPath;
    }

    public void runComplexCommand(String... params) throws IOException {
        File adb = new File(androidSdkPath + File.separator + ADB_DIRECTORY);
        Process exec = new ProcessBuilder(params)
                .directory(adb)
                .start();
        runProcess(exec);
    }

    public void runCommand(String command) throws IOException {
        command = String.format("%s/%s/ %s", androidSdkPath, ADB_DIRECTORY, command.trim());
        final Process process = Runtime.getRuntime().exec(command);
        runProcess(process);
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
            log("Line reading error", e);
        } finally {
            reader.close();
            stream.close();
        }
    }

    private void log(String message, Exception e) {
        System.out.println("message " + e.getMessage());
    }

    private void log(String tag, String message) {
        System.out.println(tag + ": " + message);
    }
}
