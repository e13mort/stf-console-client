package com.github.e13mort.stf.console;

import com.github.e13mort.stf.client.DevicesParams;

class RunOptionsBuilder {
    private String farmPropertiesFileName;
    private boolean actionPrintList;
    private String abi;
    private boolean allDevices;
    private String api;
    private String count;
    private String name;
    private boolean actionConnect;

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

    private DevicesParams createDeviceParams() throws NumberFormatException {
        DevicesParams params = new DevicesParams();
        params.setAbi(abi);
        params.setAllDevices(allDevices);
        if (api != null) {
            params.setApiVersion(Integer.parseInt(api));
        }
        if (count != null) {
            params.setCount(Integer.parseInt(count));
        }
        params.setName(name);
        // params.setDeviceId(null); - implement
        return params;
    }

    private RunOptions.Operation getOperation() {
        // implement with int flags
        if (actionPrintList == actionConnect) {
            return RunOptions.Operation.UNKNOWN;
        }
        return actionPrintList ? RunOptions.Operation.LIST :
                RunOptions.Operation.CONNECT;
    }
}