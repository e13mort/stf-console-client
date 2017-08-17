package com.github.e13mort.stf.console.commands.devices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.e13mort.stf.model.device.Device;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

final class ReflectionDeviceReader implements DeviceMapper {

    //TODO implement user columns retrieving from farm client

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writerFor(Device.class);
    private final String[] names = new String[]{"model", "abi", "serial", "sdk", "provider"};

    @Override
    public Collection<String> apply(Device device) throws Exception {
        StringWriter stringWriter = new StringWriter();
        writer.writeValue(stringWriter, device);

        JsonNode node = mapper.readTree(stringWriter.toString());
        Collection<String> strings = new ArrayList<>();
        for (String s : names) {
            JsonNode jsonNode = node.get(s);
            strings.add(jsonNode.asText("invalid"));
        }
        return strings;
    }

    @Override
    public Collection<String> getColumnNames() {
        return Arrays.asList(names);
    }
}
