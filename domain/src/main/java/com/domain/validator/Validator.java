package com.domain.validator;

import javax.xml.validation.ValidatorHandler;

public abstract class Validator {
    private final ValidatorHandler handler;

    protected Validator(ValidatorHandler handler) {
        this.handler = handler;
    }

    public abstract void validate();
    protected ValidatorHandler validatorHandler() {
        return this.handler;
    }
}
