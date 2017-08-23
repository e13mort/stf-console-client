package com.github.e13mort.stf.console;

import com.github.e13mort.stf.console.commands.EmptyDevicesException;
import com.github.e13mort.stf.console.commands.HelpCommandCreator.HelpCommandCreatorImpl;
import com.github.e13mort.stf.console.commands.UnknownCommandException;

import java.io.IOException;
import java.util.logging.*;

public class App {

    private static ErrorHandler errorHandler = throwable -> {};

    public static void main(String... args) throws IOException {
        final Logger logger = createLogger();
        errorHandler = new StfErrorHandler(logger);
        StfCommanderContext.create(logger)
                .map(context -> StfCommander.create(context, new HelpCommandCreatorImpl(), errorHandler, args))
                .subscribe(StfCommander::execute, errorHandler::handle);
    }

    static class StfErrorHandler implements ErrorHandler {

        private final Logger logger;

        StfErrorHandler(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void handle(Throwable throwable) {
            if (throwable instanceof EmptyDevicesException) {
                logger.log(Level.INFO ,"There's no devices");
            } else if (throwable instanceof UnknownCommandException) {
                logger.log(Level.INFO,"Unknown command: " + throwable);
            } else if (throwable != null) {
                logger.log(Level.INFO,"Error: ", throwable);
            }
        }

    }

    private static Logger createLogger() {
        final Logger logger = Logger.getLogger("");

        for (Handler h: logger.getHandlers()) {
            logger.removeHandler(h);
        }

        final ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleConsoleFormatter());
        logger.addHandler(handler);
        return logger;
    }
}
