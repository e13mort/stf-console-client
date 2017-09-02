package com.github.e13mort.stf.console.commands;

import com.beust.jcommander.JCommander;
import io.reactivex.Completable;

public class HelpCommand implements CommandContainer.Command {
    private final JCommander jCommander;

    public HelpCommand(JCommander jCommander) {
        this.jCommander = jCommander;
    }

    @Override
    public Completable execute() {
        return Completable.fromAction(jCommander::usage);
    }
}
