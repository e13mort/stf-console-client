package com.github.e13mort.stf.console.commands;

public class InvalidCacheIndexException extends Exception {
    private final int index;

    public InvalidCacheIndexException(int index) {
        super("Invalid cache index: " + index);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
