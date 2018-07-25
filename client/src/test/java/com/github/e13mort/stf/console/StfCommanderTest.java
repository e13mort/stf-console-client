package com.github.e13mort.stf.console;

import com.beust.jcommander.ParameterException;
import com.github.e13mort.stf.adapter.filters.StringsFilterDescription;
import com.github.e13mort.stf.client.parameters.DevicesParams;
import com.github.e13mort.stf.console.commands.UnknownCommandException;

import io.reactivex.Flowable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StfCommanderTest extends BaseStfCommanderTest {

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
    void testExceptionIsThrownWhenApiIsInvalid(DeviceParamsProducingCommand source) {
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
    void testInvalidCountPropertyThrowsAnException(DeviceParamsProducingCommand source) {
        assertThrows(ParameterException.class, test(source, "-count not_a_number"));
    }

    @DisplayName("Void count is failed to be parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testVoidCountPropertyThrowsAnException(DeviceParamsProducingCommand source) {
        assertThrows(Exception.class, test(source, "-count -l"));
    }

    @DisplayName("Name = name is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testNamePropertyIsValid(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams deviceParams = runDeviceParamsTest(source, "-name name");
        StringsFilterDescription description = deviceParams.getNameFilterDescription();
        assertLinesMatch(singletonList("name"), description.getTemplates());
    }

    @DisplayName("Name = name1,name2 is parsed in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testFewNamesPropertyIsValid(DeviceParamsProducingCommand source) throws Exception {
        DevicesParams deviceParams = runDeviceParamsTest(source, "-name name1,name2");
        StringsFilterDescription description = deviceParams.getNameFilterDescription();
        assertLinesMatch(asList("name1", "name2"), description.getTemplates());
    }

    @DisplayName("Void name is null in command")
    @ParameterizedTest(name = "Command is {0}")
    @EnumSource(DeviceParamsProducingCommand.class)
    void testVoidNameThrowsAnException(DeviceParamsProducingCommand source) {
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
        DevicesParams params = runDeviceParamsTest(source, "-serial serial1,serial2");
        assertNotNull(params.getSerialFilterDescription());
    }

    @DisplayName("Command connect with '--my' parameter will connect to an active device")
    @Test
    void testConnectToMyDevice() throws IOException {
        createCommander("connect --my").execute();
        verify(adbRunner).connectToDevice(eq(TEST_DEVICE_REMOTE));
    }

    @DisplayName("connect command is parsed with valid -l param")
    @Test
    void testConnectCommandWithValidCacheParam() throws IOException {
        createCommander("connect -l 1 2 3");
    }

    @DisplayName("connect command will throw the ParameterException if -l param is invalid")
    @Test
    void testConnectCommandWithInvalidValidCacheParam() {
         assertThrows(ParameterException.class, () -> createCommander("connect -l str"));
    }

    @DisplayName("Connect command with valid -u parameter will be parsed successfully")
    @Test
    void testValidUrlParamsIsParsed() throws IOException {
        createCommander("connect -u http://google.com");
    }

    @DisplayName("Connect command with valid -f parameter will be parsed successfully")
    @Test
    void testValidFileParamsIsParsed() throws IOException {
        createCommander("connect -f some/file.json");
    }

    @DisplayName("Connect command with invalid -u parameter will throw an error")
    @Test
    void testInvalidUrlParamsIsFailedToParse() {
        assertThrows(ParameterException.class, () -> createCommander(" connect -u not_a_url"));
    }

    @DisplayName("Connect command with empty -u parameter will throw an error")
    @Test
    void testEmptyUrlParamsIsFailedToParse() {
        assertThrows(ParameterException.class, () -> createCommander(" connect -u"));
    }

    @DisplayName("Connect command with empty -f parameter will throw an error")
    @Test
    void testEmptyFileParamsIsFailedToParse() {
        assertThrows(ParameterException.class, () -> createCommander(" connect -u"));
    }

    @DisplayName("Disconnect command call disconnectFromAllDevices method")
    @Test
    void testDisconnectCommand() throws IOException {
        createCommander("disconnect").execute();
        verify(farmClient).disconnectFromAllDevices();
    }

    @DisplayName("Disconnect command with -s will call disconnectFromDevices method with the same parameters")
    @Test
    void testDisconnectFromSpecifiedDevices() throws IOException {
        when(farmClient.disconnectFromDevices(any())).thenReturn(Flowable.empty());
        createCommander("disconnect -s ser1,ser2").execute();
        verify(farmClient).disconnectFromDevices(eq(Arrays.asList("ser1", "ser2")));
    }
}