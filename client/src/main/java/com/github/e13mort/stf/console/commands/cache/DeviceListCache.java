package com.github.e13mort.stf.console.commands.cache;

import com.github.e13mort.stf.model.device.Device;

import java.util.Collections;
import java.util.List;

public interface DeviceListCache {
    static DeviceListCache getCache() {
        return new DeviceListCacheImpl();
    }

    DeviceListCache EMPTY = new DeviceListCache() {
        @Override
        public CacheTransaction beginTransaction() {
            return CacheTransaction.EMPTY;
        }

        @Override
        public List<Device> getCachedFiles() {
            return Collections.emptyList();
        }
    };

    CacheTransaction beginTransaction();

    List<Device> getCachedFiles();

    interface CacheTransaction {
        CacheTransaction EMPTY = new CacheTransaction() {
            @Override
            public void addDevice(Device device) {

            }

            @Override
            public void save() {

            }
        };

        void addDevice(Device device);

        void save();
    }
}
