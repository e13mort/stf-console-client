package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.Parameters;
import com.github.e13mort.stf.client.FarmClient;
import io.reactivex.Completable;
import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;

@Parameters(commandDescription = "Disconnect from all of currently connected devices")
class DisconnectCommand implements CommandContainer.Command {
    private final FarmClient client;

    DisconnectCommand(FarmClient client) {
        this.client = client;
    }

    @Override
    public Completable execute() {
        return Completable.fromPublisher(client.disconnectFromAllDevices().doOnNext(this::handle));
    }

    private void handle(@NonNull Notification<String> stringNotification) {
        System.out.println("Disconnected from " + stringNotification.getValue());
    }
}
