package com.github.e13mort.stf.console.commands.connect;

import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.parameters.DevicesParams;
import com.github.e13mort.stf.client.parameters.JsonDeviceParametersReader;
import com.github.e13mort.stf.console.AdbRunner;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.logging.Logger;

import io.reactivex.Flowable;
import io.reactivex.Notification;

class FileParamsDeviceConnector extends DeviceConnector {

    private final File paramsFile;

    FileParamsDeviceConnector(FarmClient client, AdbRunner adbRunner, Logger logger, File paramsFile) {
        super(client, adbRunner, logger);
        this.paramsFile = paramsFile;
    }

    @Override
    protected Publisher<Notification<String>> createConnectionPublisher() {
        try {
            return connectWithParams(readParamsFromFile());
        } catch (JsonDeviceParametersReader.JsonParamsReaderException e) {
            return Flowable.error(e);
        }
    }

    DevicesParams readParamsFromFile() throws JsonDeviceParametersReader.JsonParamsReaderException {
        return new JsonDeviceParametersReader().read(paramsFile);
    }
}
