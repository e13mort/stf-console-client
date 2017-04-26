package com.github.e13mort.stf.console;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

class HelpCommand implements Commands.Command {
    private final Options rawOptions;

    HelpCommand(Options rawOptions) {
        this.rawOptions = rawOptions;
    }

    @Override
    public void execute(RunOptions options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("OpenSTF client", rawOptions);
    }
}
