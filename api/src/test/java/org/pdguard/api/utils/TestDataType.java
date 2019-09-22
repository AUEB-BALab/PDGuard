package org.pdguard.api.utils;

import org.junit.Assert;
import org.junit.Test;
import org.pdguard.api.utils.DataType;

public class TestDataType {
    @Test
    public void testIdentity() {
        Assert.assertTrue(DataType.IDENTITY.is(DataType.PERSONAL));
    }

    @Test
    public void testSurnameIdentity() {
        Assert.assertTrue(DataType.SURNAME.is(DataType.IDENTITY));
    }

    @Test
    public void testSurnamePersonal() {
        Assert.assertTrue(DataType.SURNAME.is(DataType.PERSONAL));
    }

    @Test
    public void testSurnameSensitive() {
        Assert.assertFalse(DataType.SURNAME.is(DataType.SENSITIVE));
    }
}
