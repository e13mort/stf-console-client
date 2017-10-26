package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.Parameter;
import com.github.e13mort.stf.adapter.filters.StringsFilterDescription;
import com.github.e13mort.stf.client.parameters.DevicesParams;

public class ConsoleDeviceParamsImpl implements DevicesParams {
    @Parameter(names = "--all", description = "Show all devices. By default only available devices are returned.")
    private boolean allDevices;
    @Parameter(names = "-abi", description = "Filter by device abi architecture")
    private String abi;
    @Parameter(names = "-api", description = "Filter by device api level")
    private int apiVersion;
    @Parameter(names = "-minApi", description = "Filter by device min api level")
    private int minApiVersion;
    @Parameter(names = "-maxApi", description = "Filter by device max api level")
    private int maxApiVersion;
    @Parameter(names = "-count", description = "Filter devices by count")
    private int count;
    @Parameter(names = "-name", description = "Filter devices by its name", converter = FilterDescriptionConverterImpl.class)
    private StringsFilterDescription nameFilterDescription;
    @Parameter(names = "-provider", description = "Filter devices by provider", converter = FilterDescriptionConverterImpl.class)
    private StringsFilterDescription providerFilterDescription;
    @Parameter(names = "-serial", description = "Filter devices by serial number", converter = FilterDescriptionConverterImpl.class)
    private StringsFilterDescription serialFilterDescription;

    @Override
    public boolean isAllDevices() {
        return allDevices;
    }

    @Override
    public String getAbi() {
        return abi;
    }

    @Override
    public int getApiVersion() {
        return apiVersion;
    }

    @Override
    public int getMinApiVersion() {
        return minApiVersion;
    }

    @Override
    public int getMaxApiVersion() {
        return maxApiVersion;
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
    public StringsFilterDescription getProviderFilterDescription() {
        return providerFilterDescription;
    }

    @Override
    public StringsFilterDescription getSerialFilterDescription() {
        return serialFilterDescription;
    }
}
