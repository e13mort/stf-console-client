package com.github.e13mort.stf.console;

import com.github.e13mort.stf.console.commands.HelpCommandCreator.HelpCommandCreatorImpl;
import com.github.e13mort.stf.console.commands.UnknownCommandException;

import java.io.IOException;

public class App {

    public static void main(String... args) throws IOException {
        try {
            StfCommanderContext context = StfCommanderContext.create();
            StfCommander stfCommander = StfCommander.create(context, new HelpCommandCreatorImpl(), args);
            stfCommander.execute();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (UnknownCommandException e) {
            System.err.println("Unknown command: " + e.getMessage());
        }
    }


}
