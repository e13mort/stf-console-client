package com.github.e13mort.stf.console;

import com.beust.jcommander.JCommander;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.client.parameters.DevicesParams;
import com.github.e13mort.stf.console.commands.CommandContainer;
import com.github.e13mort.stf.console.commands.HelpCommandCreator;
import com.github.e13mort.stf.console.commands.UnknownCommandException;
import com.github.e13mort.stf.console.commands.cache.DeviceListCache;
import com.github.e13mort.stf.model.device.Device;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BaseStfCommanderTest {
    protected static final String TEST_DEVICE_REMOTE = "127.0.0.1:15500";
    @Mock
    protected FarmClient farmClient;
    @Mock
    protected AdbRunner adbRunner;
    @Mock
    protected CommandContainer.Command helpCommand;
    @Mock
    protected DeviceListCache cache;
    @Mock
    protected ErrorHandler errorHandler;
    @Mock
    protected DeviceListCache.CacheTransaction cacheTransaction;
    @Mock
    private HelpCommandCreator helpCommandCreator;
    @Mock
    protected OutputStream outputStream;
    @Mock
    protected Logger logger;
    @Mock
    private Device myDevice;

    @BeforeEach
    protected void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(farmClient.getDevices(any(DevicesParams.class))).thenReturn(Flowable.empty());
        when(farmClient.connectToDevices(any(DevicesParams.class))).thenReturn(Flowable.empty());
        when(farmClient.disconnectFromAllDevices()).thenReturn(Flowable.empty());
        when(helpCommand.execute()).thenReturn(Completable.complete());
        when(helpCommandCreator.createHelpCommand(any(JCommander.class))).thenReturn(helpCommand);
        when(farmClient.getMyDevices()).thenReturn(Flowable.fromArray(myDevice));
        when(myDevice.getRemoteConnectUrl()).thenReturn(TEST_DEVICE_REMOTE);
        when(cache.beginTransaction()).thenReturn(cacheTransaction);
        when(cache.getCachedFiles()).thenReturn(Collections.emptyList());
    }

    protected DevicesParams runDeviceParamsTest(DeviceParamsProducingCommand source, String params) throws IOException, UnknownCommandException {
        ArgumentCaptor<DevicesParams> objectArgumentCaptor = ArgumentCaptor.forClass(DevicesParams.class);
        createCommander(source.name().toLowerCase() + " " + params).execute();
        if (source == DeviceParamsProducingCommand.DEVICES) {
            verify(farmClient).getDevices(objectArgumentCaptor.capture());
        } else {
            verify(farmClient).connectToDevices(objectArgumentCaptor.capture());
        }
        return objectArgumentCaptor.getValue();
    }

    protected Executable test(final DeviceParamsProducingCommand source, final String str) {
        return () -> runDeviceParamsTest(source, str);
    }

    protected StfCommander createCommander(String str) throws IOException {
        return StfCommander.create(new StfCommanderContext(farmClient, adbRunner, cache, outputStream, logger), helpCommandCreator, errorHandler, str.split(" "));
    }

    enum DeviceParamsProducingCommand {DEVICES, CONNECT}
}
