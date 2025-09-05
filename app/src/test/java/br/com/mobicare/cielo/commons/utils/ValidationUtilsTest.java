package br.com.mobicare.cielo.commons.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by benhur.souza on 07/04/2017.
 */

public class ValidationUtilsTest {

    @Test
    public void email_isValid_OK() throws Exception {
        assertTrue(ValidationUtils.isEmail("a@a.com"));
    }

    @Test
    public void email_isValid_NOT_OK() throws Exception {
        assertFalse(ValidationUtils.isEmail(null));
        assertFalse(ValidationUtils.isEmail(""));
        assertFalse(ValidationUtils.isEmail("a"));
        assertFalse(ValidationUtils.isEmail("a@"));
        assertFalse(ValidationUtils.isEmail("a@a"));
        assertFalse(ValidationUtils.isEmail("a@a."));
        assertFalse(ValidationUtils.isEmail("a@a.a"));
    }

    @Test
    public void password_isValid_OK() throws Exception {
        assertTrue(ValidationUtils.isValidPassword("aaaaaaa1"));
        assertTrue(ValidationUtils.isValidPassword("12345678a"));
        assertTrue(ValidationUtils.isValidPassword("aaaaaaaaaaa1"));
        assertTrue(ValidationUtils.isValidPassword("1234567890aa"));
    }

    @Test
    public void password_isValid_NOT_OK() throws Exception {
        assertFalse(ValidationUtils.isValidPassword(null));
        assertFalse(ValidationUtils.isValidPassword(""));
        assertFalse(ValidationUtils.isValidPassword("a"));
        assertFalse(ValidationUtils.isValidPassword("aa"));
        assertFalse(ValidationUtils.isValidPassword("aaa"));
        assertFalse(ValidationUtils.isValidPassword("aaaa"));
        assertFalse(ValidationUtils.isValidPassword("aaaaa"));
        assertFalse(ValidationUtils.isValidPassword("aaaaaa"));
        assertFalse(ValidationUtils.isValidPassword("aaaaaaa"));
        assertFalse(ValidationUtils.isValidPassword("aaaaaaaa"));
        assertFalse(ValidationUtils.isValidPassword("12345678"));
        assertFalse(ValidationUtils.isValidPassword("123456789101112"));
        assertFalse(ValidationUtils.isValidPassword("aaaaaaa888888"));
        assertFalse(ValidationUtils.isValidPassword("aaaaaaa888/"));
    }

    @Test
    public void cpf_isValid(){
        assertFalse(ValidationUtils.isCPF(null));
        assertFalse(ValidationUtils.isCPF(""));
        assertFalse(ValidationUtils.isCPF("123"));
        assertFalse(ValidationUtils.isCPF("12345678910"));
        assertTrue(ValidationUtils.isCPF("059.266.147.40"));
        assertTrue(ValidationUtils.isCPF("05926614740"));
    }



}
