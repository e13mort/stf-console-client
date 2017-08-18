package com.github.e13mort.stf.console.commands.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.e13mort.stf.model.device.Device;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeviceListCacheImpl implements DeviceListCache {

    private static final String PATHNAME = System.getProperty("user.home") + File.separator +  "stf-last-devices-command-result.json";

    static class CacheTransactionImpl implements CacheTransaction {

        private List<Device> devices = new ArrayList<>();

        @Override
        public void addDevice(Device device) {
            devices.add(device);
        }

        @Override
        public void save() {
            final ObjectMapper mapper = new ObjectMapper();
            final File file = new File(PATHNAME);
            if (file.exists()) {
                file.delete();
            }
            try {
                mapper.writeValue(file, devices);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public CacheTransaction beginTransaction() {
        return new CacheTransactionImpl();
    }

    @Override
    public List<Device> getCachedFiles() {
        final File file = new File(PATHNAME);
        if (!file.exists()) {
            return Collections.emptyList();
        }
        final ObjectMapper mapper = new ObjectMapper();
        Device[] devices;
        try {
            devices = mapper.readValue(file, Device[].class);
        } catch (IOException e) {
            devices = new Device[0];
        }
        return Arrays.asList(devices);
    }
}
