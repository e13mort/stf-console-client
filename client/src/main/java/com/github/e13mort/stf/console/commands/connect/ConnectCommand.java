package com.github.e13mort.stf.console.commands.connect;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.IntegerConverter;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import com.github.e13mort.stf.console.commands.CommandContainer;
import com.github.e13mort.stf.console.commands.ConsoleDeviceParamsImpl;
import com.github.e13mort.stf.console.commands.cache.DeviceListCache;
import io.reactivex.Completable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Parameters(commandDescription = "Connect to devices")
public class ConnectCommand implements CommandContainer.Command {
    private final FarmClient client;
    private final AdbRunner adbRunner;
    private final Logger logger;

    @ParametersDelegate
    private ConsoleDeviceParamsImpl params = new ConsoleDeviceParamsImpl();
    @Parameter(names = "--my", description = "Connect to currently taken devices")
    private Boolean connectToMyDevices = false;
    @Parameter(names = "-l", variableArity = true, listConverter = IntegerConverter.class,
            description = "Connect to devices by its indexes from the results of previous \"devices\" command. E.g. \"-l 1 2 5\"")
    private List<Integer> devicesIndexesFromCache = new ArrayList<>();
    @Parameter(names = "-f", description = "Read connection params from a file", converter = FileConverter.class)
    private File storedConnectionParamsFile;
    private DeviceListCache cache;

    public ConnectCommand(FarmClient client, AdbRunner adbRunner, DeviceListCache cache, Logger logger) {
        this.client = client;
        this.adbRunner = adbRunner;
        this.cache = cache;
        this.logger = logger;
    }

    @Override
    public Completable execute() {
        DeviceConnector connector = chooseConnector();
        return connector.connect();
    }

    void setDevicesIndexesFromCache(List<Integer> devicesIndexesFromCache) {
        this.devicesIndexesFromCache = devicesIndexesFromCache;
    }

    private DeviceConnector chooseConnector() {
        DeviceConnector connector;
        if (connectToMyDevices) {
            connector = new MyDevicesConnector(client, adbRunner, logger);
        } else if (!devicesIndexesFromCache.isEmpty()) {
            connector = new CacheDevicesConnector(client, adbRunner, logger, devicesIndexesFromCache, cache);
        } else if (storedConnectionParamsFile != null) {
            connector = new FileParamsDeviceConnector(client, adbRunner, logger, storedConnectionParamsFile);
        } else {
            connector = new ParamsConnector(client, adbRunner, logger, params);
        }
        return connector;
    }
}
