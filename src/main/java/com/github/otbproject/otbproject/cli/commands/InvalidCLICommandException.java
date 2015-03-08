package com.github.otbproject.otbproject.cli.commands;

/**
 * Created by justin on 19/02/2015.
 */
public class InvalidCLICommandException extends Exception {
    public InvalidCLICommandException(String message){
        super(message);
    }
}
