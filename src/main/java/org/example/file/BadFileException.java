package org.example.file;

public class BadFileException extends RuntimeException {
    public BadFileException(String message) {
        super(message);
    }
}
