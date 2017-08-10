package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import io.reactivex.Notification;
import io.reactivex.internal.operators.flowable.FlowableError;

import java.io.IOException;

@Parameters(commandDescription = "Connect to devices")
class ConnectCommand implements CommandContainer.Command {
    private static final String UNKNOWN_ERROR = "Unknown error";
    private final FarmClient client;
    private final AdbRunner adbRunner;

    @ParametersDelegate
    private ConsoleDeviceParamsImpl params = new ConsoleDeviceParamsImpl();
    @Parameter(names = "--my", description = "Connect to currently taken devices")
    private Boolean connectToMyDevices = false;

    ConnectCommand(FarmClient client, AdbRunner adbRunner) {
        this.client = client;
        this.adbRunner = adbRunner;
    }

    @Override
    public void execute() {
        if (connectToMyDevices) {
            connectToMyDevices();
        } else {
            connectToSpecifiedDevices();
        }
    }

    private void connectToMyDevices() {
        client.getMyDevices()
                .map(device -> Notification.createOnNext((String)device.getRemoteConnectUrl()))
                .switchIfEmpty(FlowableError.error(new EmptyDevicesException()))
                .subscribe(this::handleDevices, this::handleError);
    }

    private void connectToSpecifiedDevices() {
        client.connectToDevices(params)
                .subscribe(this::handleDevices, this::handleError);
    }

    private void handleDevices(Notification<String> deviceNotification) {
        if (deviceNotification.isOnNext()) {
            handleConnectedDevice(deviceNotification.getValue());
        } else if (deviceNotification.isOnError()) {
            handleNotConnectedDevice(deviceNotification.getError());
        }
    }

    private void handleConnectedDevice(String deviceIp) {
        try {
            adbRunner.connectToDevice(deviceIp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNotConnectedDevice(Throwable error) {
        System.out.println(String.format("Failed to connect: %s", error != null ? error.getMessage() : UNKNOWN_ERROR));
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof EmptyDevicesException) {
            System.out.println("There's no devices");
        } else {
            System.out.println("An error occurred during connection: " + throwable.getMessage());
        }
    }

}
