package com.github.e13mort.stf.console.commands.connect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.e13mort.stf.adapter.filters.StringsFilterDescription;
import com.github.e13mort.stf.adapter.filters.StringsFilterParser;
import com.github.e13mort.stf.client.DevicesParams;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

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
        } catch (IOException e) {
            return Flowable.error(e);
        }
    }

    DevicesParams readParamsFromFile() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(StringsFilterDescription.class, new StringFilterDescriptionDeserializer());
        module.addDeserializer(String.class, new StrictStringDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(paramsFile, JsonDevicesParams.class);
    }

    static class JsonDevicesParams implements DevicesParams {

        @JsonProperty("names")
        private StringsFilterDescription nameFilterDescription;
        @JsonProperty
        private String abi;
        @JsonProperty
        private int api;
        @JsonProperty
        private int count;
        @JsonProperty
        private int minApi;
        @JsonProperty
        private int maxApi;
        @JsonProperty("providers")
        private StringsFilterDescription providerFilterDescription;
        @JsonProperty("serials")
        private StringsFilterDescription serialFilterDescription;

        @Override
        public boolean isAllDevices() {
            //there's no sense to be able to connect to unavailable devices
            return false;
        }

        @Override
        public String getAbi() {
            return abi;
        }

        @Override
        public int getApiVersion() {
            return api;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public StringsFilterDescription getNameFilterDescription() {
            return nameFilterDescription;
        }

        @Override
        public int getMinApiVersion() {
            return minApi;
        }

        @Override
        public int getMaxApiVersion() {
            return maxApi;
        }

        @Override
        public StringsFilterDescription getProviderFilterDescription() {
            return providerFilterDescription;
        }

        @Override
        public StringsFilterDescription getSerialFilterDescription() {
            return serialFilterDescription;
        }
    }

    static class StringFilterDescriptionDeserializer extends JsonDeserializer<StringsFilterDescription> {

        private StringsFilterParser parser = new StringsFilterParser();

        @Override
        public StringsFilterDescription deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            final String value = p.getCodec().readValue(p, String.class);
            return parser.parse(value);
        }
    }

    static class StrictStringDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
                return p.getText();
            }
            throw ctxt.mappingException("Invalid token type: " + p.getCurrentToken());
        }
    }
}
