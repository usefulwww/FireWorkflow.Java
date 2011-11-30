/*
 * Copyright 2007-2009 非也
 * All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.fireflow.engine.calendar;

import java.util.Date;
import java.util.Properties;

import org.fireflow.engine.RuntimeContext;
import org.fireflow.model.Duration;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on 2009-03-11
 */
public class DefaultCalendarServiceTest {

    public DefaultCalendarServiceTest() {
    }

    /**
     * Test of dateAfter method, of class DefaultCalendarService.
     */
    @Test
    public void testDateAfter() {
        System.out.println("dateAfter");
        Long temp = System.currentTimeMillis();
        Date fromDate = new Date(temp);
        Duration duration = new Duration(60,Duration.SECOND);
        DefaultCalendarService instance = new DefaultCalendarService();
        Date result = instance.dateAfter(fromDate, duration);
        assertNotNull(result);
        Date expResult = new Date(temp+60*1000);
        assertEquals (expResult, result);
    }

    /**
     * Test of businessDateAfter method, of class DefaultCalendarService.
     */
    @Test
    public void testBusinessDateAfter() {
        System.out.println("businessDateAfter");
        Date fromDate = new Date();
        int totalDurationInMillseconds = 0;
        DefaultCalendarService instance = new DefaultCalendarService();
        Date result = instance.businessDateAfter(fromDate, totalDurationInMillseconds);
        assertNotNull(result);
       
        Date expResult = new Date();
        assertEquals(expResult, result);

    }

    /**
     * Test of getTotalWorkingTime method, of class DefaultCalendarService.
     */
    @Test
    public void testGetTotalWorkingTime() {
        System.out.println("getTotalWorkingTime");
        Date date = null;
        DefaultCalendarService instance = new DefaultCalendarService();
        int expResult = 0;
        int result = instance.getTotalWorkingTime(date);
        assertEquals(expResult, result);
    }

    /**
     * Test of isBusinessDay method, of class DefaultCalendarService.
     */
    @Test
    public void testIsBusinessDay() {
        System.out.println("isBusinessDay");
        Date d = null;
        DefaultCalendarService instance = new DefaultCalendarService();
        boolean expResult = false;
        boolean result = instance.isBusinessDay(d);
        assertEquals(expResult, result);

    }

    /**
     * Test of getBusinessCalendarProperties method, of class DefaultCalendarService.
     */
    @Test
    public void testGetBusinessCalendarProperties() {
        System.out.println("getBusinessCalendarProperties");
        DefaultCalendarService instance = new DefaultCalendarService();
        Properties result = instance.getBusinessCalendarProperties();
        assertNotNull(result);
	
        Properties expResult = new Properties();
        expResult.put("day_format", "yyyy-MM-dd");
        expResult.put("hour_format", "HH:mm");
        expResult.put("hours_of_business_day", "7.5");
        expResult.put("business_time", "8:30-12:00 & 13:30-17:30");
        
        assertEquals(expResult, result);
    }

    /**
     * Test of setBusinessCalendarProperties method, of class DefaultCalendarService.
     */
    @Test
    public void testSetBusinessCalendarProperties() {
        System.out.println("setBusinessCalendarProperties");
        Properties props = new Properties();
        DefaultCalendarService instance = new DefaultCalendarService();
        instance.setBusinessCalendarProperties(props);

    }

    /**
     * Test of getSysDate method, of class DefaultCalendarService.
     */
    @Test
    public void testGetSysDate() {
        System.out.println("getSysDate");
        DefaultCalendarService instance = new DefaultCalendarService();
        Date result = instance.getSysDate();
        assertNotNull(result);
        
        Date expResult = new Date();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRuntimeContext method, of class DefaultCalendarService.
     */
    @Test
    public void testSetRuntimeContext() {
        System.out.println("setRuntimeContext");
        RuntimeContext ctx = null;
        DefaultCalendarService instance = new DefaultCalendarService();
        instance.setRuntimeContext(ctx);
    }

    /**
     * Test of getRuntimeContext method, of class DefaultCalendarService.
     */
    @Test
    public void testGetRuntimeContext() {
        System.out.println("getRuntimeContext");
        DefaultCalendarService instance = new DefaultCalendarService();
        RuntimeContext expResult = null;
        RuntimeContext result = instance.getRuntimeContext();
        assertEquals(expResult, result);
    }

}