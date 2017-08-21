package com.github.e13mort.stf.console;

import com.github.e13mort.stf.console.commands.EmptyDevicesException;
import com.github.e13mort.stf.console.commands.HelpCommandCreator.HelpCommandCreatorImpl;
import com.github.e13mort.stf.console.commands.UnknownCommandException;
import io.reactivex.Single;

import java.io.IOException;

public class App {

    private static ErrorHandler errorHandler = App::handleError;

    public static void main(String... args) throws IOException {
        Single.just(StfCommanderContext.create())
                .map(context -> StfCommander.create(context, new HelpCommandCreatorImpl(), errorHandler, args))
                .subscribe(StfCommander::execute, App::handleError);
    }

    private static void handleError(Throwable throwable) {
        if (throwable instanceof EmptyDevicesException) {
            System.err.println("There's no devices");
        } else if (throwable instanceof UnknownCommandException) {
            System.err.println("Unknown command: " + throwable.getMessage());
        } else if (throwable != null) {
            System.err.println("Error: " + throwable.getMessage());
        }
    }
}
