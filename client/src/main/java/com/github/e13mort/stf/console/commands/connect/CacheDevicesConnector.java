package com.github.e13mort.stf.console.commands.connect;

import com.github.e13mort.stf.adapter.filters.InclusionType;
import com.github.e13mort.stf.adapter.filters.StringsFilterDescription;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.parameters.DevicesParams;
import com.github.e13mort.stf.client.parameters.DevicesParamsImpl;
import com.github.e13mort.stf.console.AdbRunner;
import com.github.e13mort.stf.console.commands.cache.DeviceListCache;
import com.github.e13mort.stf.model.device.Device;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.logging.Logger;

class CacheDevicesConnector extends DeviceConnector {

    private final List<Integer> devicesIndexesFromCache;
    private final DeviceListCache cache;

    protected CacheDevicesConnector(FarmClient client, AdbRunner runner, Logger logger, List<Integer> devicesIndexesFromCache, DeviceListCache cache) {
        super(client, runner, logger);
        this.devicesIndexesFromCache = devicesIndexesFromCache;
        this.cache = cache;
    }

    @Override
    protected Publisher<Notification<String>> createConnectionPublisher() {
        return readParamsFromCache(cache.getCachedFiles()).flatMap(this::connectWithParams);
    }

    private Flowable<DevicesParams> readParamsFromCache(List<Device> cachedFiles) {
        return Flowable.fromIterable(devicesIndexesFromCache)
                .doOnNext(integer -> validate(cachedFiles, integer))
                .map(integer -> --integer)
                .map(cachedFiles::get)
                .map(Device::getSerial)
                .toList()
                .map(this::createStringsFilterDescription)
                .map(this::createDevicesParams)
                .toFlowable();
    }

    private void validate(List<Device> cachedFiles, int index) throws InvalidCacheIndexException {
        if (!isIndexValid(cachedFiles, index)) {
            throw new InvalidCacheIndexException(index);
        }
    }

    private boolean isIndexValid(List<Device> cachedFiles, Integer integer) {
        return integer > 0 && integer <= cachedFiles.size();
    }

    private StringsFilterDescription createStringsFilterDescription(List<String> l) {
        return new StringsFilterDescription(InclusionType.INCLUDE, l);
    }

    private DevicesParams createDevicesParams(StringsFilterDescription filter) {
        final DevicesParamsImpl params = new DevicesParamsImpl();
        params.setSerialFilterDescription(filter);
        return params;
    }
}
