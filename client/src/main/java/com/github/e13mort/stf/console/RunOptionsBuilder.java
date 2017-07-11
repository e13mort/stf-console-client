package com.github.e13mort.stf.console;

import com.github.e13mort.stf.adapter.filters.StringsFilterDescription;
import com.github.e13mort.stf.adapter.filters.StringsFilterParser;
import com.github.e13mort.stf.client.DevicesParams;

import java.util.Arrays;
import java.util.List;

class RunOptionsBuilder {
    private static final String DELIMITER = ",";
    private String farmPropertiesFileName;
    private boolean actionPrintList;
    private String abi;
    private boolean allDevices;
    private String api;
    private String minApi;
    private String maxApi;
    private String count;
    private String name;
    private boolean actionConnect;
    private boolean actionDisconnect;
    private String rawProviderTemplate;
    private String rawSerialNumberTemplate;

    RunOptionsBuilder setFarmPropertiesFileName(String farmProprtiesFileName) {
        this.farmPropertiesFileName = farmProprtiesFileName;
        return this;
    }

    RunOptionsBuilder setActionPrintList(boolean actionPrintList) {
        this.actionPrintList = actionPrintList;
        return this;
    }

    RunOptionsBuilder setAbi(String abi) {
        this.abi = abi;
        return this;
    }

    RunOptionsBuilder setActionDisconnect(boolean actionDisconnect) {
        this.actionDisconnect = actionDisconnect;
        return this;
    }

    RunOptionsBuilder setAll(boolean allDevices) {
        this.allDevices = allDevices;
        return this;
    }

    RunOptionsBuilder setApi(String api) {
        this.api = api;
        return this;
    }

    RunOptions createRunOptions() {
        return new RunOptions(farmPropertiesFileName, getOperation(), createDeviceParams());
    }

    RunOptionsBuilder setCount(String count) {
        this.count = count;
        return this;
    }

    RunOptionsBuilder setName(String name) {
        this.name = name;
        return this;
    }

    RunOptionsBuilder setActionConnect(boolean actionConnect) {
        this.actionConnect = actionConnect;
        return this;
    }

    RunOptionsBuilder setMinApi(String minApi) {
        this.minApi = minApi;
        return this;
    }

    RunOptionsBuilder setMaxApi(String maxApi) {
        this.maxApi = maxApi;
        return this;
    }

    RunOptionsBuilder setProviderTemplate(String rawProviderTemplate) {
        this.rawProviderTemplate = rawProviderTemplate;
        return this;
    }

    RunOptionsBuilder setSerialNumberTemplate(String rawSerialNumberTemplate) {
        this.rawSerialNumberTemplate = rawSerialNumberTemplate;
        return this;
    }

    private DevicesParams createDeviceParams() throws NumberFormatException {
        DevicesParams params = new DevicesParams();
        params.setAbi(abi);
        params.setAllDevices(allDevices);
        if (api != null) {
            params.setApiVersion(Integer.parseInt(api));
        }
        if (minApi != null) {
            params.setMinApiVersion(Integer.parseInt(minApi));
        }
        if (maxApi != null) {
            params.setMaxApiVersion(Integer.parseInt(maxApi));
        }
        if (count != null) {
            params.setCount(Integer.parseInt(count));
        }
        if (name != null) {
            params.setNames(getNames(name));
        }
        if (rawProviderTemplate != null) {
            setupProvider(params, rawProviderTemplate);
        }
        if (rawSerialNumberTemplate != null) {
            setupSerialNumber(params, rawSerialNumberTemplate);
        }
        // params.setDeviceId(null); - implement
        return params;
    }

    private List<String> getNames(String name) {
        return Arrays.asList(name.split(DELIMITER));
    }

    private void setupSerialNumber(DevicesParams params, String rawSerialNumberTemplate) {
        StringsFilterDescription description = getStringsFilterDescription(rawSerialNumberTemplate);
        if (description != null) {
            params.setSerialFilterDescription(description);
        }
    }

    private void setupProvider(DevicesParams params, String template) {
        StringsFilterDescription description = getStringsFilterDescription(template);
        if (description != null) {
            params.setProviderFilterDescription(description);
        }
    }

    private StringsFilterDescription getStringsFilterDescription(String template) {
        try {
            return new StringsFilterParser().parse(template);
        } catch (Exception e) {
            //todo log this
            return null;
        }
    }

    private RunOptions.Operation getOperation() {
        // implement with int flags
        if (actionDisconnect) {
            return RunOptions.Operation.DISCONNECT;
        }
        if (actionConnect) {
            return RunOptions.Operation.CONNECT;
        }
        if (actionPrintList) {
            return RunOptions.Operation.LIST;
        }
        return RunOptions.Operation.UNKNOWN;
    }
}