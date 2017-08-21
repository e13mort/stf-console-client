package com.github.e13mort.stf.console.commands;

import com.github.e13mort.stf.client.FarmClient;
import com.github.e13mort.stf.console.AdbRunner;
import com.github.e13mort.stf.console.commands.cache.DeviceListCache;
import com.github.e13mort.stf.console.commands.devices.DevicesCommand;
import io.reactivex.Completable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandContainer {

    private Map<String, Command> commandMap = new HashMap<>();

    public CommandContainer(final FarmClient client, AdbRunner adbRunner, DeviceListCache cache) {
        commandMap.put("devices", new DevicesCommand(client, cache));
        commandMap.put("connect", new ConnectCommand(client, adbRunner, cache));
        commandMap.put("disconnect", new DisconnectCommand(client));
    }

    public Command getCommand(String operation) {
        return commandMap.get(operation);
    }

    public Collection<String> getAllCommands() {
        return commandMap.keySet();
    }

    public interface Command {
        Completable execute();
    }

}
