package com.bizpulse.app
import com.bizpulse.app.util.Validation
import org.junit.Assert.*
import org.junit.Test
class ValidationTest {
    @Test fun usernameRules() {assertTrue(Validation.username("jed_123"));assertFalse(Validation.username("ab"));assertFalse(Validation.username("a b c"))}
    @Test fun minimumPasswordLength() {assertTrue(Validation.password("123456"));assertFalse(Validation.password("12345"))}
    @Test fun positiveFinitePrice() {assertTrue(Validation.price(0.01));assertFalse(Validation.price(0.0));assertFalse(Validation.price(-1.0));assertFalse(Validation.price(Double.NaN));assertFalse(Validation.price(Double.POSITIVE_INFINITY))}
    @Test fun stockAndPaymentValidation() {assertFalse(Validation.count(-1));assertTrue(Validation.payment("QR"));assertFalse(Validation.payment("GCash"));assertFalse(Validation.payment("Card"));assertFalse(Validation.payment("Unknown"))}
}
