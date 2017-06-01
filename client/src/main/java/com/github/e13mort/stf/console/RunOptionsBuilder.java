package com.github.e13mort.stf.console;

import com.github.e13mort.stf.adapter.filters.ProviderDescription;
import com.github.e13mort.stf.adapter.filters.ProviderStringParser;
import com.github.e13mort.stf.client.DevicesParams;

class RunOptionsBuilder {
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
        params.setName(name);
        if (rawProviderTemplate != null) {
            setupProvider(params, rawProviderTemplate);
        }
        // params.setDeviceId(null); - implement
        return params;
    }

    private void setupProvider(DevicesParams params, String template) {
        ProviderStringParser parser = new ProviderStringParser();
        try {
            ProviderDescription description = parser.parse(template);
            params.setProviderDescription(description);
        } catch (Exception e) {
            //todo log this
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