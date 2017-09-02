package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.Parameters;
import com.github.e13mort.stf.client.FarmClient;
import io.reactivex.Completable;
import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;

import java.util.logging.Level;
import java.util.logging.Logger;

@Parameters(commandDescription = "Disconnect from all of currently connected devices")
class DisconnectCommand implements CommandContainer.Command {
    private final FarmClient client;
    private final Logger logger;

    DisconnectCommand(FarmClient client, Logger logger) {
        this.client = client;
        this.logger = logger;
    }

    @Override
    public Completable execute() {
        return Completable.fromPublisher(client.disconnectFromAllDevices().doOnNext(this::handle));
    }

    private void handle(@NonNull Notification<String> stringNotification) {
        logger.log(Level.INFO, "Disconnected from: {0}", stringNotification.getValue());
    }
}
