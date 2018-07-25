package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.e13mort.stf.client.FarmClient;
import io.reactivex.Completable;
import io.reactivex.Notification;
import io.reactivex.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Parameters(commandDescription = "Disconnect from all of currently connected devices")
class DisconnectCommand implements CommandContainer.Command {
    private final FarmClient client;
    private final Logger logger;

    @Parameter(description = "Disconnect from the specified devices", names = "-s")
    private List<String> serials = new ArrayList<>();

    DisconnectCommand(FarmClient client, Logger logger) {
        this.client = client;
        this.logger = logger;
    }

    @Override
    public Completable execute() {
        if (serials.isEmpty()) return disconnectFromAll();
        return disconnectFromDevices(serials);
    }

    private Completable disconnectFromDevices(List<String> serials) {
        return Completable.fromPublisher(client.disconnectFromDevices(serials).doOnNext(this::handle));
    }

    private Completable disconnectFromAll() {
        return Completable.fromPublisher(client.disconnectFromAllDevices().doOnNext(this::handle));
    }

    private void handle(@NonNull Notification<String> stringNotification) {
        logger.log(Level.INFO, "Disconnected from: {0}", stringNotification.getValue());
    }
}
