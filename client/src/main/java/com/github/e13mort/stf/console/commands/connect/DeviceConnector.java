package com.github.e13mort.stf.console.commands.connect;

import com.github.e13mort.stf.client.DevicesParams;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import com.github.e13mort.stf.console.commands.EmptyDevicesException;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import org.reactivestreams.Publisher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class DeviceConnector {
    protected final FarmClient client;
    protected final AdbRunner runner;
    protected final Logger logger;

    protected DeviceConnector(FarmClient client, AdbRunner runner, Logger logger) {
        this.client = client;
        this.runner = runner;
        this.logger = logger;
    }

    Completable connect() {
        return Completable.fromPublisher(createConnectionPublisher());
    }

    protected abstract Publisher<Notification<String>> createConnectionPublisher();

    protected Flowable<Notification<String>> getEmptyError() {
        return Flowable.error(new EmptyDevicesException());
    }

    protected void handleDevices(Notification<String> deviceNotification) {
        if (deviceNotification.isOnNext()) {
            handleConnectedDevice(deviceNotification.getValue());
        } else if (deviceNotification.isOnError()) {
            handleNotConnectedDevice(deviceNotification.getError());
        }
    }

    protected void handleConnectedDevice(String deviceIp) {
        try {
            runner.connectToDevice(deviceIp);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to connect to a device", e);
        }
    }

    protected void handleNotConnectedDevice(Throwable error) {
        logger.log(Level.WARNING, "Failed to lock a device", error);
    }

    protected Flowable<Notification<String>> connectWithParams(DevicesParams params) {
        return client.connectToDevices(params)
                .doOnNext(this::handleDevices)
                .switchIfEmpty(getEmptyError());
    }
}
