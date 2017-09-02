package com.github.e13mort.stf.console;

public interface ErrorHandler {
    ErrorHandler EMPTY = error -> {};

    void handle(Throwable throwable);
}
