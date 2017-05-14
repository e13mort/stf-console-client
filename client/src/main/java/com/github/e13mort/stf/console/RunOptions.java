package com.github.e13mort.stf.console;

import com.github.e13mort.stf.client.DevicesParams;
import org.apache.commons.cli.*;

class RunOptions {
    private static final String OPTION_PROPERTIES = "p";
    private static final String OPTION_FILTER_ABI = "abi";
    private static final String OPTION_FILTER_API = "api";
    private static final String OPTION_FILTER_MIN_API = "minApi";
    private static final String OPTION_FILTER_MAX_API = "maxApi";
    private static final String OPTION_FILTER_ACTIVE = "all";
    private static final String OPTION_COMMAND_LIST = "l";
    private static final String OPTION_COMMAND_CONNECT = "c";
    private static final String OPTION_COMMAND_DISCONNECT = "d";
    private static final String OPTION_COUNT = "count";
    private static final String DEFAULT_PROPERTY_FILE_NAME = "farm.properties";
    private static final String OPTION_FILTER_NAME = "n";

    private final String farmPropertiesFileName;
    private final Operation operation;
    private DevicesParams deviceParams;

    RunOptions(String farmPropertiesFileName, Operation operation, DevicesParams params) {
        this.farmPropertiesFileName = farmPropertiesFileName;
        this.operation = operation;
        this.deviceParams = params;
    }

    static RunOptions create(Options options, String... params) throws ParseException {
        DefaultParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, params);

        return new RunOptionsBuilder()
                .setFarmPropertiesFileName(line.getOptionValue(OPTION_PROPERTIES, DEFAULT_PROPERTY_FILE_NAME))
                .setActionPrintList(line.hasOption(OPTION_COMMAND_LIST))
                .setActionConnect(line.hasOption(OPTION_COMMAND_CONNECT))
                .setActionDisconnect(line.hasOption(OPTION_COMMAND_DISCONNECT))
                .setAbi(line.getOptionValue(OPTION_FILTER_ABI))
                .setAll(line.hasOption(OPTION_FILTER_ACTIVE))
                .setApi(line.getOptionValue(OPTION_FILTER_API))
                .setMinApi(line.getOptionValue(OPTION_FILTER_MIN_API))
                .setMaxApi(line.getOptionValue(OPTION_FILTER_MAX_API))
                .setCount(line.getOptionValue(OPTION_COUNT))
                .setName(line.getOptionValue(OPTION_FILTER_NAME))
                .createRunOptions();
    }

    String getFarmPropertiesFileName() {
        return farmPropertiesFileName;
    }

    Operation getOperation() {
        return operation;
    }

    DevicesParams getDeviceParams() {
        return deviceParams;
    }

    static Options createOptions() {
        Options options = new Options();
        options.addOption(OPTION_PROPERTIES, "prop", true, "Farm properties file path");
        options.addOption(OPTION_COMMAND_LIST, "list", false, "LIST devices");
        options.addOption(OPTION_COMMAND_CONNECT, "connect", false, "connect to devices");
        options.addOption(OPTION_COMMAND_DISCONNECT, "disconnect", false, "disconnect from all of currently connected devices");
        options.addOption(OPTION_FILTER_ACTIVE, "all", false, "Show all devices. By default only available devices are returned.");
        options.addOption(OPTION_FILTER_API, "api", true, "Filter by device api level");
        options.addOption(OPTION_FILTER_MIN_API, "minApi", true, "Filter by device min api level");
        options.addOption(OPTION_FILTER_MAX_API, "maxApi", true, "Filter by device max api level");
        options.addOption(OPTION_FILTER_ABI, "abi", true, "Filter by device abi architecture");
        options.addOption(OPTION_COUNT, "count", true, "Filter devices by count");
        options.addOption(OPTION_FILTER_NAME, "name", true, "Filter devices by its name");
        return options;
    }

    public enum Operation {
        UNKNOWN, LIST, CONNECT, DISCONNECT
    }
}
