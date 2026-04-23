package org.satellite.dev.progiple.sateevents.exceptions;

public class ParserFormatIdException extends RuntimeException {
    public ParserFormatIdException(String id) {
        super("Идентификатор парсера " + id + " может содержать только буквы, цифры и дефисы!");
    }
}
