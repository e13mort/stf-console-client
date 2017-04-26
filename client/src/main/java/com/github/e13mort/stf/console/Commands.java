package com.github.e13mort.stf.console;

import com.github.e13mort.stf.client.FarmClient;
import org.apache.commons.cli.Options;

import java.util.HashMap;
import java.util.Map;

class Commands {

    private Map<RunOptions.Operation, Command> commandMap = new HashMap<>();

    Commands(final FarmClient client, Options options, AdbRunner adbRunner) {
        commandMap.put(RunOptions.Operation.UNKNOWN, new HelpCommand(options));
        commandMap.put(RunOptions.Operation.LIST, new ListCommand(client));
        commandMap.put(RunOptions.Operation.CONNECT, new ConnectCommand(client, adbRunner));
    }

    void run(RunOptions options) {
        getCommand(options.getOperation()).execute(options);
    }

    private Command getCommand(RunOptions.Operation operation) {
        return commandMap.get(operation != null ? operation : RunOptions.Operation.UNKNOWN);
    }

    interface Command {
        void execute(RunOptions options);
    }

}
