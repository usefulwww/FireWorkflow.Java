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
package org.fireflow.engine.event;

import org.fireflow.engine.IProcessInstance;

/**
 * 流程实例事件
 * @author 非也,nychen2000@163.com
 *
 */
public class ProcessInstanceEvent {

    /**
     * 在即将启动流程实例的时候触发的事件
     */
    public static final int BEFORE_PROCESS_INSTANCE_RUN = 2;
    
    /**
     * 在流程实例结束后触发的事件
     */
    public static final int AFTER_PROCESS_INSTANCE_COMPLETE = 7;
    int eventType = -1;
    IProcessInstance source = null;

    /**
     * 返回触发事件的流程实例
     * @return
     */
    public IProcessInstance getSource() {
        return source;
    }

    public void setSource(IProcessInstance source) {
        this.source = source;
    }

    /**
     * 返回事件类型，取值为BEFORE_PROCESS_INSTANCE_RUN或者AFTER_PROCESS_INSTANCE_COMPLETE
     * @return
     */
    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
