package com.github.e13mort.stf.console.commands.devices;

import com.github.e13mort.stf.model.device.Device;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

final class PredefinedDeviceReader implements DeviceMapper {

    private static final List<String> PREDEFINED_COLUMN_NAMES = Arrays.asList("Name", "Abi", "Serial", "Sdk", "Provider");

    @Override
    public Collection<String> apply(Device device) throws Exception {
        return Arrays.asList(
                device.getModel(),
                device.getAbi(),
                device.getSerial(),
                String.valueOf(device.getSdk()),
                device.getProvider().getName()
        );
    }

    @Override
    public Collection<String> getColumnNames() {
        return PREDEFINED_COLUMN_NAMES;
    }
}
