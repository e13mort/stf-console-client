package com.github.e13mort.stf.console.commands.devices;

import com.github.e13mort.stf.model.device.Device;

import java.util.Arrays;
import java.util.Collection;

final class FallbackDeviceReader implements DeviceMapper {

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
        return Arrays.asList("Name", "Abi", "Serial", "Sdk", "Provider");
    }
}
