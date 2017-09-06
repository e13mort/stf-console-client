package com.github.e13mort.stf.console.commands.connect;

import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import org.reactivestreams.Publisher;

import java.util.logging.Logger;

class MyDevicesConnector extends DeviceConnector {

    public MyDevicesConnector(FarmClient client, AdbRunner runner, Logger logger) {
        super(client, runner, logger);
    }

    @Override
    protected Publisher<Notification<String>> createConnectionPublisher() {
        return connectToMyDevices();
    }

    private Flowable<Notification<String>> connectToMyDevices() {
        return client.getMyDevices()
                .map(device -> Notification.createOnNext((String) device.getRemoteConnectUrl()))
                .switchIfEmpty(getEmptyError())
                .doOnNext(this::handleDevices);
    }
}
