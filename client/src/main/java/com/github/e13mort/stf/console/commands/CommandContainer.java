package com.github.e13mort.stf.console.commands;

import com.github.e13mort.stf.console.StfCommanderContext;
import com.github.e13mort.stf.console.commands.devices.DevicesCommand;
import io.reactivex.Completable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandContainer {

    private Map<String, Command> commandMap = new HashMap<>();

    public CommandContainer(StfCommanderContext c) {
        commandMap.put("devices", new DevicesCommand(c.getClient(), c.getCache(), c.getOutput()));
        commandMap.put("connect", new ConnectCommand(c.getClient(), c.getAdbRunner(), c.getCache(), c.getLogger()));
        commandMap.put("disconnect", new DisconnectCommand(c.getClient(), c.getLogger()));
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
