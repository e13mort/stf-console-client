package com.github.e13mort.stf.console.commands.connect;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.e13mort.stf.client.DevicesParams;
import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileParamsDeviceConnectorTest {
    @Mock
    private FarmClient farmClient;
    @Mock
    private AdbRunner adbRunner;
    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("valid params files parsing")
    @ParameterizedTest(name = "file: {0}")
    @ValueSource(strings = {
            "valid_min_api.json",
            "valid_max_api.json",
            "valid_api.json",
            "valid_count.json",
            "valid_abi.json",
            "valid_names.json",
            "valid_providers.json",
            "valid_serials.json"
    })
    void readParamsFromFile_validFiles_success(String testFileName) throws IOException {
        final FileParamsDeviceConnector connector = createConnector(testFileName);
        connector.readParamsFromFile();
    }

    @DisplayName("invalid params should throw exception")
    @ParameterizedTest(name = "param file: {0} -> exception: {1}")
    @MethodSource("invalidFiles")
    void readParamsFromFile_invalidFiles_jsonException(String fileName, Class<Throwable> exceptionClass) {
        final FileParamsDeviceConnector connector = createConnector(fileName);
        Assertions.assertThrows(exceptionClass, connector::readParamsFromFile);
    }

    @DisplayName("full valid connection params file should be parsed")
    @Test
    void readParamsFromFile_validFullFile_exactMatching() throws IOException {
        final FileParamsDeviceConnector connector = createConnector("valid_full_params.json");
        final DevicesParams params = connector.readParamsFromFile();
        assertEquals(10, params.getCount());
        assertEquals(10, params.getApiVersion());
        assertEquals(10, params.getMinApiVersion());
        assertEquals(10, params.getMaxApiVersion());
        assertEquals("arm", params.getAbi());
        assertNotNull(params.getSerialFilterDescription());
        assertNotNull(params.getNameFilterDescription());
        assertNotNull(params.getProviderFilterDescription());
    }

    private static Stream<Arguments> invalidFiles() {
        return Stream.of(
                Arguments.of("invalid_field_name.json", JsonMappingException.class),
                Arguments.of("invalid_field_value_simple.json", JsonMappingException.class),
                Arguments.of("invalid_field_value_custom.json", JsonMappingException.class),
                Arguments.of("not_a_file", IOException.class)
        );
    }

    private FileParamsDeviceConnector createConnector(String testFileName) {
        final File paramsFile = getFile(testFileName);
        return new FileParamsDeviceConnector(farmClient, adbRunner, logger, paramsFile);
    }

    private File getFile(String testFileName) {
        final URL resource = getClass().getClassLoader().getResource("connection_params/" + testFileName);
        return new File(resource != null ? resource.getFile() : "not_existing_file");
    }

}