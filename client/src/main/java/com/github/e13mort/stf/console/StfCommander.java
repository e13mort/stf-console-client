package com.github.e13mort.stf.console;

import com.beust.jcommander.JCommander;
import com.github.e13mort.stf.console.commands.CommandContainer;
import com.github.e13mort.stf.console.commands.HelpCommandCreator;
import com.github.e13mort.stf.console.commands.UnknownCommandException;
import io.reactivex.Completable;

import java.io.IOException;

public class StfCommander {
    private final CommandContainer commandContainer;
    private final CommandContainer.Command defaultCommand;
    private final ErrorHandler errorHandler;
    private String commandName;

    private StfCommander(String commandName, CommandContainer commandContainer, CommandContainer.Command defaultCommand, ErrorHandler errorHandler) {
        this.commandName = commandName;
        this.commandContainer = commandContainer;
        this.defaultCommand = defaultCommand;
        this.errorHandler = errorHandler;
    }

    static StfCommander create(StfCommanderContext context, HelpCommandCreator commandCreator, ErrorHandler errorHandler, String... args) throws IOException {
        CommandContainer commandContainer = new CommandContainer(context.getClient(), context.getAdbRunner(), context.getCache());
        JCommander commander = createCommander(commandContainer, args);
        return new StfCommander(commander.getParsedCommand(), commandContainer, commandCreator.createHelpCommand(commander), errorHandler);
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

    void execute() {
        CommandContainer.Command command = chooseCommand();
        if (command != null) {
            run(command.execute());
        } else {
            errorHandler.handle(new UnknownCommandException(commandName));
        }
    }

    private void run(Completable completable) {
        completable.subscribe(() -> {}, errorHandler::handle);
    }

    private CommandContainer.Command chooseCommand() {
        if (commandName == null) {
            return defaultCommand;
        }
        return commandContainer.getCommand(commandName);
    }
}
