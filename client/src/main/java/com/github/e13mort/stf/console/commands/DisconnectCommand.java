package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.Parameters;
import com.github.e13mort.stf.client.FarmClient;
import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

@Parameters(commandDescription = "Disconnect from all of currently connected devices")
class DisconnectCommand implements CommandContainer.Command {
    private final FarmClient client;

    DisconnectCommand(FarmClient client) {
        this.client = client;
    }

    @Override
    public void execute() {
        client.disconnectFromAllDevices().subscribe(new Consumer<Notification<String>>() {
            @Override
            public void accept(@NonNull Notification<String> stringNotification) throws Exception {
                System.out.println("Disconnected from " + stringNotification.getValue());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                System.err.println(throwable.getMessage());
            }
        });
    }
}
