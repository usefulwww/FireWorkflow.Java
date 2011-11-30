/**
 * Copyright 2007-2008 非也
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.calendar;

import java.util.Date;

import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.model.Duration;


/**
 * 日历服务
 * @author 非也，nychen2000@163.com
 */
public interface ICalendarService extends IRuntimeContextAware{

    /**
     * 获得fromDate后相隔duration的某个日期
     * Get the date after the duration
     * @param duration
     * @return
     */
    public Date dateAfter(Date fromDate, Duration duration);

    /**
     * 缺省实现，周六周日都是非工作日，其他的都为工作日。
     * 实际应用中，可以在数据库中建立一张非工作日表，将周末以及法定节假日录入其中，
     * 然后在该方法中读该表的数据来判断工作日和非工作日。
     * @param d
     * @return
     */
    public boolean isBusinessDay(Date d);

    /**
     * 获得系统当前时间
     * @return
     */
    public Date getSysDate();
}
