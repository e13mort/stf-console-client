package com.github.e13mort.stf.console.commands.devices;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.e13mort.stf.client.DevicesParams;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.commands.CommandContainer;
import com.github.e13mort.stf.console.commands.ConsoleDeviceParamsImpl;

@Parameters(commandDescription = "Print list of available devices")
public class DevicesCommand implements CommandContainer.Command {
    private final FarmClient client;

    @ParametersDelegate
    private DevicesParams params = new ConsoleDeviceParamsImpl();

    @Parameter(names = {"--my-columns"}, description = "<BETA> Use columns from web panel")
    private boolean userColumns;

    public DevicesCommand(FarmClient client) {
        this.client = client;
    }

    @Override
    public void execute() {
        DocumentsLoader loader = new DocumentsLoader(client, params);
        loader.setFieldsReader(userColumns ? new ReflectionDeviceReader() : new FallbackDeviceReader());

        TablePrinter tablePrinter = new TablePrinter(loader.getColumnNames());
        loader.loadDevices().subscribe(tablePrinter::addDevice, this::printError, () -> tablePrinter.print(System.out));
    }

    private void printError(Throwable throwable) {
        System.err.println(throwable.getMessage());
    }

}
