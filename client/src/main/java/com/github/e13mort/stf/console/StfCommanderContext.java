package com.github.e13mort.stf.console;

import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.FarmInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

class StfCommanderContext {
    private static final String DEFAULT_PROPERTY_FILE_NAME = "farm.properties";

    private final FarmClient client;
    private final AdbRunner adbRunner;

    public StfCommanderContext(FarmClient client, AdbRunner adbRunner) {
        this.client = client;
        this.adbRunner = adbRunner;
    }

    public FarmClient getClient() {
        return client;
    }

    public AdbRunner getAdbRunner() {
        return adbRunner;
    }

    public static StfCommanderContext create() throws IOException {
        FarmInfo farmInfo = createFarmInfo();
        FarmClient client = FarmClient.create(farmInfo);
        AdbRunner adbRunner = new AdbRunner(farmInfo.getSdkPath());
        return new StfCommanderContext(client, adbRunner);
    }

    private static FarmInfo createFarmInfo() throws IOException {
        File properties = getPropertiesFile(DEFAULT_PROPERTY_FILE_NAME);
        return readFarmInfo(properties);
    }

    private static FarmInfo readFarmInfo(File propertiesFile) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(propertiesFile));
        String farmUrl = properties.getProperty("stf.url");
        String apiKey = properties.getProperty("stf.key");
        if (farmUrl == null || apiKey == null) {
            throw new IllegalArgumentException("Property file is invalid");
        }
        String sdk = properties.getProperty("android_sdk");
        int timeout = getTimeout(properties);
        return new FarmInfo(farmUrl, apiKey, sdk, timeout);
    }

    private static File getPropertiesFile(String propertiesFileName) {
        File file = new File(propertiesFileName);

        if (!file.exists()) {
            File homeFile = new File(System.getProperty("user.home"), propertiesFileName);
            if (homeFile.exists()) {
                return homeFile;
            } else {
                throw new IllegalArgumentException("Property file does not exists");
            }
        }
        return file;
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
