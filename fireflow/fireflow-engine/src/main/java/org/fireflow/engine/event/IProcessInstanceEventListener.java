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

import org.fireflow.engine.EngineException;

/**
 * 流程实例事件监听接口
 * @author 非也,nychen2000@163.com
 *
 */
public interface IProcessInstanceEventListener {
	/**
	 * 响应流程实例的事件。通过e.getEventType来判断事件的类型。
	 * 流程实例有两种事件：<br/>
	 * BEFORE_PROCESS_INSTANCE_RUN (= 2):在即将启动流程实例的时候触发的事件<br/>
	 * AFTER_PROCESS_INSTANCE_COMPLETE (= 7):在流程实例结束后触发的事件
	 * @param e 流程实例事件
	 * @throws EngineException
	 */
	public void onProcessInstanceEventFired(ProcessInstanceEvent e)throws EngineException;
}
