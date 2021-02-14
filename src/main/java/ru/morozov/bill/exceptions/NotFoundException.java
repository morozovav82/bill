package ru.morozov.bill.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(Long id) {
        super("Not found by id=" + id);
    }
}
