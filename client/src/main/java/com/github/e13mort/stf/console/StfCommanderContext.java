package com.github.e13mort.stf.console;

import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.FarmInfo;
import com.github.e13mort.stf.console.commands.cache.DeviceListCache;
import io.reactivex.Single;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class StfCommanderContext {
    private static final String DEFAULT_PROPERTY_FILE_NAME = "farm.properties";

    private final FarmClient client;
    private final AdbRunner adbRunner;
    private DeviceListCache cache;
    private OutputStream output;
    private Logger logger;

    StfCommanderContext(FarmClient client, AdbRunner adbRunner, DeviceListCache cache, OutputStream output, Logger logger) {
        this.client = client;
        this.adbRunner = adbRunner;
        this.cache = cache;
        this.output = output;
        this.logger = logger;
    }

    public FarmClient getClient() {
        return client;
    }

    public AdbRunner getAdbRunner() {
        return adbRunner;
    }

    public DeviceListCache getCache() {
        return cache;
    }

    public OutputStream getOutput() {
        return output;
    }

    public Logger getLogger() {
        return logger;
    }

    static Single<StfCommanderContext> create(Logger logger) {
        return Single.create(e -> e.onSuccess(createInternal(logger)));
    }

    private static StfCommanderContext createInternal(Logger logger) throws IOException {
        FarmInfo farmInfo = createFarmInfo();
        FarmClient client = FarmClient.create(farmInfo);
        AdbRunner adbRunner = new AdbRunner(farmInfo.getSdkPath(), logger);
        DeviceListCache cache = DeviceListCache.getCache();
        final OutputStream output = System.out;
        return new StfCommanderContext(client, adbRunner, cache, output, logger);
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
        String timeoutProperty = properties.getProperty("stf.timeout");
        return Integer.parseInt(timeoutProperty);
    }
}
