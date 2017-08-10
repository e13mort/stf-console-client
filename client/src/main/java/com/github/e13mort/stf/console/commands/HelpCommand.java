package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.JCommander;

public class HelpCommand implements CommandContainer.Command {
    private final JCommander jCommander;

    public HelpCommand(JCommander jCommander) {
        this.jCommander = jCommander;
    }

    @Override
    public void execute() {
        jCommander.usage();
    }
}
