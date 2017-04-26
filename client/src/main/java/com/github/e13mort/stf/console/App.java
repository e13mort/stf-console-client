package com.github.e13mort.stf.console;

import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.FarmInfo;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.*;
import java.util.Properties;

public class App {

    public static void main(String... args) throws ParseException, IOException {

        Options options = RunOptions.createOptions();

        RunOptions runOptions = RunOptions.create(options, args);

        FarmInfo farmInfo = createFarmInfo(runOptions.getFarmPropertiesFileName());

        Commands commands = new Commands(FarmClient.create(farmInfo), options, new AdbRunner(farmInfo.getSdkPath()));

        commands.run(runOptions);
    }

    private static FarmInfo createFarmInfo(String propertiesFileName) throws IOException {
        if (propertiesFileName == null) {
            throw new NullPointerException("Property file name is null");
        }

        File file = new File(propertiesFileName);

        if (!file.exists()) {
            File homeFile = new File(System.getProperty("user.home"), propertiesFileName);
            if (homeFile.exists()) {
                propertiesFileName = homeFile.getAbsolutePath();
            } else {
                throw new IllegalArgumentException("Property file does not exists");
            }
        }

        Properties properties = new Properties();
        properties.load(new FileReader(propertiesFileName));
        String farmUrl = properties.getProperty("stf.url");
        String apiKey = properties.getProperty("stf.key");
        if (farmUrl == null || apiKey == null) {
            throw new IllegalArgumentException("Property file is invalid");
        }
        String sdk = properties.getProperty("android_sdk");
        int timeout = getTimeout(properties);
        return new FarmInfo(farmUrl, apiKey, sdk, timeout);
    }

    private static int getTimeout(Properties properties) {
        try {
            String timeoutProperty = properties.getProperty("stf.timeout");
            return Integer.parseInt(timeoutProperty);
        } catch (NumberFormatException e) {
            System.err.println("Failed to get timeout from the properties. A default value is going to be used.");
        }
        return -1;
    }


}
