package com.github.e13mort.stf.console.commands.connect;

import com.github.e13mort.stf.client.parameters.DevicesParams;
import com.github.e13mort.stf.console.BaseStfCommanderTest;
import com.github.e13mort.stf.model.device.Device;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Detailed \"connect\" command cases")
class ConnectCommandTest extends BaseStfCommanderTest {

    private static final int MOCK_OBJECT_COUNT = 4;
    private List<Device> cachedDevices = new ArrayList<>();
    private ArgumentCaptor<DevicesParams> paramsCaptor = ArgumentCaptor.forClass(DevicesParams.class);
    private ConnectCommand connectCommand;

    @BeforeEach
    @Override
    protected void setUp() throws IOException {
        super.setUp();
        when(cache.getCachedFiles()).thenReturn(cachedDevices);
        when(farmClient.connectToDevices(any(DevicesParams.class))).thenReturn(Flowable.just((Notification.createOnNext("url"))));
        connectCommand = new ConnectCommand(farmClient, adbRunner, cache, logger);
    }

    @DisplayName("Valid index leads to DeviceParams instance creation")
    @ParameterizedTest(name = "\"connect -l {0}\" creates DeviceParams with serial {1}")
    @MethodSource("validIndexArrayProvider")
    void connectToEachCacheItem(int parameter, String targetSerial) throws IOException {
        prepareMocks();
        connectCommand.setDevicesIndexesFromCache(Collections.singletonList(parameter));
        connectCommand.execute().test().assertComplete();
        verify(farmClient).connectToDevices(paramsCaptor.capture());
        assertDeviceInParams(targetSerial);
    }

    @DisplayName("Invalid index in cache handled with exception")
    @ParameterizedTest(name = "\"connect -l {0}\" command should call errorHandler with the InvalidCacheIndexException with index {1}")
    @MethodSource("invalidIndexArrayProvider")
    void exceptionIsPropagatedIfSomeIndexInvalid(List<Integer> arrayWithInvalidIndex, int invalidIndex) throws IOException {
        prepareMocks();
        connectCommand.setDevicesIndexesFromCache(arrayWithInvalidIndex);
        connectCommand.execute().test()
                .assertError(throwable -> ((InvalidCacheIndexException) throwable).getIndex() == invalidIndex);
    }

    private static Stream<Arguments> invalidIndexArrayProvider() {
        return Stream.of(Arguments.of(Arrays.asList(0, 1, 2), 0), Arguments.of(Arrays.asList(1, 2, 7), 7));
    }

    private static Stream<Arguments> validIndexArrayProvider() {
        return Stream.of(
                Arguments.of(1, "serial_1"),
                Arguments.of(2, "serial_2"),
                Arguments.of(3, "serial_3"),
                Arguments.of(4, "serial_4"));
    }

    @SuppressWarnings("SameParameterValue")
    private void assertDeviceInParams(String serial) {
        assertEquals(serial, paramsCaptor.getValue().getSerialFilterDescription().getTemplates().get(0));
    }

    private void prepareMocks() {
        for (int i = 1; i <= MOCK_OBJECT_COUNT; i++) {
            final Device mock = mock(Device.class);
            when(mock.getSerial()).thenReturn("serial_" + i);
            cachedDevices.add(mock);
        }
    }

}
