package com.github.e13mort.stf.console.commands.connect;

import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.parameters.DevicesParams;
import com.github.e13mort.stf.client.parameters.JsonDeviceParametersReader;
import com.github.e13mort.stf.console.AdbRunner;

import org.reactivestreams.Publisher;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import io.reactivex.Flowable;
import io.reactivex.Notification;

class FileParamsDeviceConnector extends DeviceConnector {

    private final ParametersReader reader;

    static DeviceConnector of(FarmClient client, AdbRunner adbRunner, Logger logger, File paramsFile) {
        return new FileParamsDeviceConnector(client, adbRunner, logger, () -> new JsonDeviceParametersReader().read(paramsFile));
    }

    static DeviceConnector of(FarmClient client, AdbRunner adbRunner, Logger logger, URL paramsUrl) {
        return new FileParamsDeviceConnector(client, adbRunner, logger, () -> new JsonDeviceParametersReader().read(paramsUrl));
    }

    private FileParamsDeviceConnector(FarmClient client, AdbRunner adbRunner, Logger logger, ParametersReader reader) {
        super(client, adbRunner, logger);
        this.reader = reader;
    }

    @Override
    protected Publisher<Notification<String>> createConnectionPublisher() {
        try {
            return connectWithParams(reader.read());
        } catch (JsonDeviceParametersReader.JsonParamsReaderException e) {
            return Flowable.error(e);
        }
    }

    private interface ParametersReader {
        DevicesParams read() throws JsonDeviceParametersReader.JsonParamsReaderException;
    }
}
