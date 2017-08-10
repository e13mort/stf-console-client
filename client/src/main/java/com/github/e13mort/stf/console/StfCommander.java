package com.github.e13mort.stf.console;

import com.beust.jcommander.JCommander;
import com.github.e13mort.stf.console.commands.CommandContainer;
import com.github.e13mort.stf.console.commands.HelpCommandCreator;
import com.github.e13mort.stf.console.commands.UnknownCommandException;

import java.io.IOException;

public class StfCommander {
    private final CommandContainer commandContainer;
    private final CommandContainer.Command defaultCommand;
    private String commandName;

    private StfCommander(String commandName, CommandContainer commandContainer, CommandContainer.Command defaultCommand) {
        this.commandName = commandName;
        this.commandContainer = commandContainer;
        this.defaultCommand = defaultCommand;
    }

    static StfCommander create(StfCommanderContext context, HelpCommandCreator commandCreator, String... args) throws IOException {
        CommandContainer commandContainer = new CommandContainer(context.getClient(), context.getAdbRunner());
        JCommander commander = createCommander(commandContainer, args);
        return new StfCommander(commander.getParsedCommand(), commandContainer, commandCreator.createHelpCommand(commander));
    }

    private static JCommander createCommander(CommandContainer commandContainer, String[] args) {
        JCommander.Builder builder = JCommander.newBuilder();
        for (String operation : commandContainer.getAllCommands()) {
            CommandContainer.Command command = commandContainer.getCommand(operation);
            builder.addCommand(operation, command);
        }
        JCommander commander = builder.build();
        commander.setProgramName("stf");
        commander.setCaseSensitiveOptions(false);
        commander.parseWithoutValidation(args);
        return commander;
    }

    void execute() throws UnknownCommandException {
        CommandContainer.Command command = chooseCommand();
        if (command == null) {
            throw new UnknownCommandException(commandName);
        }
        command.execute();
    }

    private CommandContainer.Command chooseCommand() {
        if (commandName == null) {
            return defaultCommand;
        }
        return commandContainer.getCommand(commandName);
    }
}
