package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import com.github.e13mort.stf.model.device.Device;
import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.operators.flowable.FlowableError;

import java.io.IOException;

@Parameters(commandDescription = "Connect to devices")
class ConnectCommand implements CommandContainer.Command {
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
                .map(new Function<Device, Notification<String>>() {
                    @Override
                    public Notification<String> apply(Device device) throws Exception {
                        return Notification.createOnNext((String)device.getRemoteConnectUrl());
                    }
                })
                .switchIfEmpty(FlowableError.<Notification<String>>error(new EmptyDevicesException()))
                .subscribe(new ConnectionNotificationSubscriber(adbRunner), new ThrowableConsumer());
    }

    private void connectToSpecifiedDevices() {
        client.connectToDevices(params)
                .subscribe(new ConnectionNotificationSubscriber(adbRunner), new ThrowableConsumer());
    }

    private static class ConnectionNotificationSubscriber implements Consumer<Notification<String>> {

        static final String UNKNOWN_ERROR = "Unknown error";
        private final AdbRunner adbRunner;

        ConnectionNotificationSubscriber(AdbRunner adbRunner) {
            this.adbRunner = adbRunner;
        }

        @Override
        public void accept(@NonNull Notification<String> deviceNotification) throws Exception {
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
    }

    private static class ThrowableConsumer implements Consumer<Throwable> {
        @Override
        public void accept(@NonNull Throwable throwable) throws Exception {
            if (throwable instanceof EmptyDevicesException) {
                System.out.println("There's no devices");
            } else {
                System.out.println("An error occurred during connection: " + throwable.getMessage());
            }
        }
    }

    private static class EmptyDevicesException extends Exception { }
}
