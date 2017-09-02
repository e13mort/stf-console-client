package com.github.e13mort.stf.console.commands.devices;

import com.github.e13mort.stf.client.DevicesParams;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.commands.cache.DeviceListCache;
import io.reactivex.Flowable;

import java.util.Collection;

class DocumentsLoader {

    private final FarmClient client;
    private final DevicesParams params;
    private DeviceMapper fieldsReader = DeviceMapper.EMPTY;
    private DeviceListCache deviceListCache = DeviceListCache.EMPTY;

    public DocumentsLoader(FarmClient client, DevicesParams params) {
        this.client = client;
        this.params = params;
    }

    public void setFieldsReader(DeviceMapper fieldsReader) {
        this.fieldsReader = fieldsReader;
    }

    public Collection<String> getColumnNames() {
        return fieldsReader.getColumnNames();
    }

    Flowable<Collection<String>> loadDevices() {
        final DeviceListCache.CacheTransaction transaction = deviceListCache.beginTransaction();
        return client
                .getDevices(params)
                .doOnNext(transaction::addDevice)
                .map(fieldsReader)
                .doOnComplete(transaction::save);
    }

    public void setDeviceListCache(DeviceListCache deviceListCache) {
        this.deviceListCache = deviceListCache;
    }
}
