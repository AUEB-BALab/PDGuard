package org.pdguard.eagent.common;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestPeriodValidator {
    private class PeriodValidator implements PeriodValidity { }

    @Test
    public void testGetExpirationDate() {
        Date validFrom = new Date();
        PeriodValidator validity = new PeriodValidator();
        Date validTo = validity.getExpirationDate(validFrom);
        Calendar cal = Calendar.getInstance();
        cal.setTime(validFrom);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(validTo);
        assertEquals(cal2.get(Calendar.YEAR) - cal.get(Calendar.YEAR), 2);
    }

    @Test
    public void testIsExpired() {
        Date validFrom = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(validFrom);
        final int twoYearsAfter = 2;
        calendar.add(Calendar.YEAR, twoYearsAfter);
        Date validTo = calendar.getTime();
        PeriodValidator validity = new PeriodValidator();
        assertFalse(validity.isExpired(validFrom, validTo));
        assertTrue(validity.isExpired(validTo, validFrom));
    }
}
