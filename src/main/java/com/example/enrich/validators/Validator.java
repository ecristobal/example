package com.example.enrich.validators;

public interface Validator<T> {

    boolean validate(T input);

}
