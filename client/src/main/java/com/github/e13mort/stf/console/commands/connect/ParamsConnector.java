package com.github.e13mort.stf.console.commands.connect;

import com.github.e13mort.stf.client.DevicesParams;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import io.reactivex.Notification;
import org.reactivestreams.Publisher;

import java.util.logging.Logger;

class ParamsConnector extends DeviceConnector {

    private final DevicesParams params;

    protected ParamsConnector(FarmClient client, AdbRunner runner, Logger logger, DevicesParams params) {
        super(client, runner, logger);
        this.params = params;
    }

    @Override
    protected Publisher<Notification<String>> createConnectionPublisher() {
        return connectWithParams(params);
    }

}
