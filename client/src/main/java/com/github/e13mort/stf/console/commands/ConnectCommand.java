package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.beust.jcommander.converters.IntegerConverter;
import com.github.e13mort.stf.adapter.filters.InclusionType;
import com.github.e13mort.stf.adapter.filters.StringsFilterDescription;
import com.github.e13mort.stf.client.DevicesParams;
import com.github.e13mort.stf.client.DevicesParamsImpl;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import com.github.e13mort.stf.console.commands.cache.DeviceListCache;
import com.github.e13mort.stf.model.device.Device;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import io.reactivex.Single;
import io.reactivex.internal.operators.flowable.FlowableError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "Connect to devices")
class ConnectCommand implements CommandContainer.Command {
    private static final String UNKNOWN_ERROR = "Unknown error";
    private final FarmClient client;
    private final AdbRunner adbRunner;

    @ParametersDelegate
    private ConsoleDeviceParamsImpl params = new ConsoleDeviceParamsImpl();
    @Parameter(names = "--my", description = "Connect to currently taken devices")
    private Boolean connectToMyDevices = false;
    @Parameter(names = "-l", variableArity = true, listConverter = IntegerConverter.class)
    private List<Integer> devicesIndexesFromCache = new ArrayList<>();
    private DeviceListCache cache;

    ConnectCommand(FarmClient client, AdbRunner adbRunner, DeviceListCache cache) {
        this.client = client;
        this.adbRunner = adbRunner;
        this.cache = cache;
    }

    @Override
    public Completable execute() {
        Flowable<Notification<String>> publisher;
        if (connectToMyDevices) {
            publisher = connectToMyDevices();
        } else if (devicesIndexesFromCache.isEmpty()) {
            publisher = connectWithParams(params);
        } else {
            publisher = readParamsFromCache().flatMapPublisher(this::connectWithParams);
        }
        return Completable.fromPublisher(publisher.switchIfEmpty(getError()));
    }

    private Flowable<Notification<String>> getError() {
        return FlowableError.error(new EmptyDevicesException());
    }

    private Single<DevicesParams> readParamsFromCache() {
        return Flowable.fromIterable(devicesIndexesFromCache)
                .map(integer -> --integer)
                .map(cache.getCachedFiles()::get)
                .map(Device::getSerial)
                .toList()
                .map(this::createStringsFilterDescription)
                .map(this::createDevicesParams);
    }

    private StringsFilterDescription createStringsFilterDescription(List<String> l) {
        return new StringsFilterDescription(InclusionType.INCLUDE, l);
    }

    private DevicesParams createDevicesParams(StringsFilterDescription filter) {
        final DevicesParamsImpl params = new DevicesParamsImpl();
        params.setSerialFilterDescription(filter);
        return params;
    }

    private Flowable<Notification<String>> connectToMyDevices() {
        return client.getMyDevices()
                .map(device -> Notification.createOnNext((String) device.getRemoteConnectUrl()))
                .doOnNext(this::handleDevices);
    }

    private Flowable<Notification<String>> connectWithParams(DevicesParams params) {
        return client.connectToDevices(params).doOnNext(this::handleDevices);
    }

    private void handleDevices(Notification<String> deviceNotification) {
        if (deviceNotification.isOnNext()) {
            handleConnectedDevice(deviceNotification.getValue());
        } else if (deviceNotification.isOnError()) {
            handleNotConnectedDevice(deviceNotification.getError());
        }
    }

    private void handleConnectedDevice(String deviceIp) {
        try {
            adbRunner.connectToDevice(deviceIp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNotConnectedDevice(Throwable error) {
        System.err.println(String.format("Failed to connect: %s", error != null ? error.getMessage() : UNKNOWN_ERROR));
    }

}
