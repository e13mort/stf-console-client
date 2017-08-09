package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.JCommander;

public interface HelpCommandCreator {
    CommandContainer.Command createHelpCommand(JCommander commander);

    class HelpCommandCreatorImpl implements HelpCommandCreator {
        @Override
        public CommandContainer.Command createHelpCommand(JCommander commander) {
            return new HelpCommand(commander);
        }
    }
}


