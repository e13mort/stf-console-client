package com.github.e13mort.stf.console;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.github.e13mort.stf.adapter.filters.StringsFilterDescription;
import com.github.e13mort.stf.client.DevicesParams;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.commands.CommandContainer;
import com.github.e13mort.stf.console.commands.HelpCommandCreator;
import com.github.e13mort.stf.console.commands.UnknownCommandException;
import com.github.e13mort.stf.model.device.Device;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StfCommanderTest {

    @Mock
    private FarmClient farmClient;
    @Mock
    private AdbRunner adbRunner;
    @Mock
    private HelpCommandCreator helpCommandCreator;
    @Mock
    private CommandContainer.Command helpCommand;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(farmClient.getDevices(any(DevicesParams.class))).thenReturn(Flowable.<Device>empty());
        when(farmClient.connectToDevices(any(DevicesParams.class))).thenReturn(Flowable.<Notification<String>>empty());
        when(farmClient.disconnectFromAllDevices()).thenReturn(Flowable.<Notification<String>>empty());
        when(helpCommandCreator.createHelpCommand(any(JCommander.class))).thenReturn(helpCommand);
    }

    @DisplayName("Command without params should be called with non null DeviceParams object")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void commandsEmptyParamsNonNullResult(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams value = runDeviceParamsTest(source, "");
        assertNotNull(value);
    }

    @DisplayName("Command without params should be called with an empty DeviceParams object")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void commandsEmptyParamsNullableFields(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams value = runDeviceParamsTest(source, "");
        assertNull(value.getAbi());
        assertNull(value.getNameFilterDescription());
        assertNull(value.getProviderFilterDescription());
        assertNull(value.getSerialFilterDescription());
        assertEquals(0, value.getApiVersion());
        assertEquals(0, value.getMaxApiVersion());
        assertEquals(0, value.getMinApiVersion());
        assertEquals(0, value.getCount());
        assertFalse(value.isAllDevices());
    }

    @DisplayName("test_abi is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testReadAbiFromValidString(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams params = runDeviceParamsTest(source, "-abi test_abi");
        assertEquals("test_abi", params.getAbi());
    }

    @DisplayName("test_abi is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testAllDevicesEnabledByFlag(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams params = runDeviceParamsTest(source, "--all");
        assertTrue(params.isAllDevices());
    }

    @DisplayName("Unspecified api in command will set it to 0")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testApiVersionIsZeroInUnrelatedString(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams devicesParams = runDeviceParamsTest(source, "");
        assertEquals(0, devicesParams.getApiVersion());
    }

    @DisplayName("Specified api in command will set it up")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testApiVersionIsValidInParameter(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams params = runDeviceParamsTest(source, "-api 19");
        assertEquals(19, params.getApiVersion());
    }

    @DisplayName("Min api = 19 is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testMinApiVersionIsValidInParameter(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams params = runDeviceParamsTest(source, "-minApi 19");
        assertEquals(19, params.getMinApiVersion());
    }

    @DisplayName("Max api = 19 is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testMaxApiVersionIsValidInParameter(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams params = runDeviceParamsTest(source, "-maxApi 19");
        assertEquals(19, params.getMaxApiVersion());
    }

    @DisplayName("Max api = not_a_number is failed to be parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testExceptionIsThrownWhenApiIsInvalid(DeviceParamsProducingCommand source) throws Exception {
        assertThrows(ParameterException.class, test(source, "-api not_a_number"));
    }

    @DisplayName("Count = 10 is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testCountPropertyIsValid(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams params = runDeviceParamsTest(source, "-count 10");
        assertEquals(10, params.getCount());
    }

    @DisplayName("Count = not_a_number is failed to be parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testInvalidCountPropertyThrowsAnException(DeviceParamsProducingCommand source) throws Exception {
        assertThrows(ParameterException.class, test(source, "-count not_a_number"));
    }

    @DisplayName("Void count is failed to be parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testVoidCountPropertyThrowsAnException(DeviceParamsProducingCommand source) throws Exception {
        assertThrows(Exception.class, test(source, "-count -l"));
    }

    @DisplayName("Name = name is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testNamePropertyIsValid(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams deviceParams = runDeviceParamsTest(source, "-n name");
        StringsFilterDescription description = deviceParams.getNameFilterDescription();
        assertLinesMatch(singletonList("name"), description.getTemplates());
    }

    @DisplayName("Name = name1,name2 is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testFewNamesPropertyIsValid(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams deviceParams = runDeviceParamsTest(source, "-n name1,name2");
        StringsFilterDescription description = deviceParams.getNameFilterDescription();
        assertLinesMatch(asList("name1", "name2"), description.getTemplates());
    }

    @DisplayName("Void name is null in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testVoidNameThrowsAnException(DeviceParamsProducingCommand source) throws Exception {
        assertThrows(Exception.class, test(source, "-n"));
    }

    @DisplayName("Call app without any param will execute the help command")
    @Test
    void testEmptyCommandParamsWillExecuteHelp() throws Exception {
        createCommander("").execute();
        verify(helpCommand).execute();
    }

    @DisplayName("Call app without any param will execute the help command")
    @Test
    void test() throws Exception {
        createCommander("unknown").execute();
        verify(helpCommand).execute();
    }

    @DisplayName("Provider = p1 is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testProviderDescriptionNotNullWithParameter(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams params = runDeviceParamsTest(source, "-provider p1");
        assertNotNull(params.getProviderFilterDescription());
    }

    @DisplayName("Serial = serial1,serial2 is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testSerialNumberDescriptionNotNullWithParameter(DeviceParamsProducingCommand source) throws IOException, UnknownCommandException {
        DevicesParams params = runDeviceParamsTest(source, "-sn serial1,serial2");
        assertNotNull(params.getSerialFilterDescription());
    }

    @Test
    void testDisconnectCommand() throws IOException, UnknownCommandException {
        createCommander("disconnect").execute();
        verify(farmClient).disconnectFromAllDevices();
    }

    private DevicesParams runDeviceParamsTest(DeviceParamsProducingCommand source, String params) throws IOException, UnknownCommandException {
        ArgumentCaptor<DevicesParams> objectArgumentCaptor = ArgumentCaptor.forClass(DevicesParams.class);
        createCommander(source.name().toLowerCase() + " " + params).execute();
        if (source == DeviceParamsProducingCommand.DEVICES) {
            verify(farmClient).getDevices(objectArgumentCaptor.capture());
        } else {
            verify(farmClient).connectToDevices(objectArgumentCaptor.capture());
        }
        return objectArgumentCaptor.getValue();
    }

    enum DeviceParamsProducingCommand {DEVICES, CONNECT}

    private Executable test(final DeviceParamsProducingCommand source, final String str) {
        return new Executable() {
            @Override
            public void execute() throws Throwable {
                runDeviceParamsTest(source, str);
            }
        };
    }

    private Executable test(@SuppressWarnings("SameParameterValue") final String str) {
        return new Executable() {
            @Override
            public void execute() throws Throwable {
                createCommander(str).execute();
            }
        };
    }

    private StfCommander createCommander(String str) throws IOException {
        return StfCommander.create(new StfCommanderContext(farmClient, adbRunner), helpCommandCreator, str.split(" "));
    }
}