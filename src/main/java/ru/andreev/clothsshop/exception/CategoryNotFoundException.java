package ru.andreev.clothsshop.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Category with ID " + id + " not found.");
    }
}
