package com.domain.order;


import com.domain.validator.Validator;

import javax.xml.validation.ValidatorHandler;

public class OrderValidator extends Validator {

    protected OrderValidator(ValidatorHandler handler) {
        super(handler);
    }

    @Override
    public void validate() {

    }
}
