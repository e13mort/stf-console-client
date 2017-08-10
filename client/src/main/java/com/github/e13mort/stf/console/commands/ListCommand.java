package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.model.device.Device;

@Parameters(commandDescription = "Print list of available devices")
class ListCommand implements CommandContainer.Command {
    private static final String UNKNOWN_DEVICE_NAME = "<Unknown>";
    private final FarmClient client;

    @ParametersDelegate
    private ConsoleDeviceParamsImpl params = new ConsoleDeviceParamsImpl();

    ListCommand(FarmClient client) {
        this.client = client;
    }

    @Override
    public void execute() {
        client.getDevices(params).subscribe(this::print, this::print);
    }

    private void print(Device device) {
        String name = device.getName();
        name = name != null ? name : UNKNOWN_DEVICE_NAME;
        System.out.println(String.format(
                "%10s abi: %7s serial: %s, sdk: %s",
                name, device.getAbi(), device.getSerial(), device.getSdk()));
    }

    private void print(Throwable throwable) {
        System.err.println(throwable.getMessage());
    }
}
