package com.github.e13mort.stf.console.commands.devices;

import com.github.e13mort.stf.model.device.Device;
import io.reactivex.functions.Function;

import java.util.Collection;
import java.util.Collections;

interface DeviceMapper extends Function<Device, Collection<String>> {
    DeviceMapper EMPTY = new DeviceMapper() {
        @Override
        public Collection<String> getColumnNames() {
            return Collections.emptyList();
        }

        @Override
        public Collection<String> apply(Device device) throws Exception {
            return Collections.emptyList();
        }
    };

    Collection<String> getColumnNames();
}
