/*
 * Copyright (C) 2008 feilong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feilong.core.date.dateutiltest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feilong.core.date.BaseDateUtilTest;
import com.feilong.core.date.DateUtil;

public class DateUtilGetWeekOfYearTest extends BaseDateUtilTest{

    /** The Constant log. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtilGetWeekOfYearTest.class);
    //******com.feilong.core.date.DateUtil.getWeekOfYear(Date)************************************

    /**
     * Test get week of year null date.
     */
    @Test(expected = NullPointerException.class)
    public void testGetWeekOfYearNullDate(){
        DateUtil.getWeekOfYear(null);
    }

    /**
     * Test get day of year null date.
     */
    //************com.feilong.core.date.DateUtil.getDayOfYear(Date)************************************
    @Test(expected = NullPointerException.class)
    public void testGetDayOfYearNullDate(){
        DateUtil.getDayOfYear(null);
    }
}