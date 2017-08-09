package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import java.io.IOException;

@Parameters(commandDescription = "Connect to devices")
class ConnectCommand implements CommandContainer.Command {
    private final FarmClient client;
    private final AdbRunner adbRunner;

    @ParametersDelegate
    private ConsoleDeviceParamsImpl params = new ConsoleDeviceParamsImpl();

    ConnectCommand(FarmClient client, AdbRunner adbRunner) {
        this.client = client;
        this.adbRunner = adbRunner;
    }

    @Override
    public void execute() {
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
                adbRunner.runComplexCommand("adb", "connect", deviceIp);
                adbRunner.runComplexCommand("adb", "wait-for-device");
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
            System.out.println("An error occurred during connection: " + throwable.getMessage());
        }
    }
}
