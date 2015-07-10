package com.github.otbproject.otbproject.web;

public class WarDownloadException extends Exception {
    WarDownloadException(String message) {
        super(message);
    }

    WarDownloadException(Throwable source) {
        super(source);
    }
}
