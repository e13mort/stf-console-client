package com.github.e13mort.stf.console.commands.devices;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.parameters.DevicesParams;
import com.github.e13mort.stf.console.commands.CommandContainer;
import com.github.e13mort.stf.console.commands.ConsoleDeviceParamsImpl;
import com.github.e13mort.stf.console.commands.cache.DeviceListCache;
import io.reactivex.Completable;

import java.io.OutputStream;

@Parameters(commandDescription = "Print list of available devices")
public class DevicesCommand implements CommandContainer.Command {
    private final FarmClient client;
    private final OutputStream output;

    @ParametersDelegate
    private DevicesParams params = new ConsoleDeviceParamsImpl();

    @Parameter(names = {"--my-columns"}, description = "<BETA> Use columns from web panel")
    private boolean userColumns;
    private DeviceListCache cache;

    public DevicesCommand(FarmClient client, DeviceListCache cache, OutputStream output) {
        this.client = client;
        this.cache = cache;
        this.output = output;
    }

    @Override
    public Completable execute() {
        DocumentsLoader loader = new DocumentsLoader(client, params);
        loader.setFieldsReader(userColumns ? new ReflectionDeviceReader() : new PredefinedDeviceReader());
        loader.setDeviceListCache(cache);

        TablePrinter tablePrinter = new TablePrinter(loader.getColumnNames());
        return Completable.fromPublisher(
                loader.loadDevices()
                        .doOnNext(tablePrinter::addDevice)
                        .doOnComplete(() -> tablePrinter.print(output)));
    }
}
