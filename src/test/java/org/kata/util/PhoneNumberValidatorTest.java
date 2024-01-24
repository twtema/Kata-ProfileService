package org.kata.util;

import org.junit.Assert;
import org.junit.Test;
import org.kata.exception.InvalidPhoneNumberException;

import static org.junit.Assert.fail;

public class PhoneNumberValidatorTest  {
    @Test
    public void testValidPhoneNumbers() {
        try {
            PhoneNumberValidator.validatePhoneNumbers("+7 (999) 123-45-67");
            PhoneNumberValidator.validatePhoneNumbers("8-915-678-90-12");
            PhoneNumberValidator.validatePhoneNumbers("+7(495)1234567");
        } catch (InvalidPhoneNumberException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testInvalidPhoneNumbers() {
        Assert.assertThrows(InvalidPhoneNumberException.class, () -> PhoneNumberValidator.validatePhoneNumbers("(929)657+04"));
        Assert.assertThrows(InvalidPhoneNumberException.class, () -> PhoneNumberValidator.validatePhoneNumbers("8(929)-64-34"));
        Assert.assertThrows(InvalidPhoneNumberException.class, () -> PhoneNumberValidator.validatePhoneNumbers("8-999-123"));
    }

}