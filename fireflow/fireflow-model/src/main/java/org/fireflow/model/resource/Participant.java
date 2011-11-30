/**
 * Copyright 2007-2008 陈乜云（非也,Chen Nieyun）
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
package org.fireflow.model.resource;

/**
 * 参与者。
 * @author 非也
 *
 */
public class Participant extends AbstractResource {

	/**
	 * 
	 * 任务分配句柄的类名。<br/>
	 * Fire workflow引擎调用该句柄获得真正的操作者ID。	 * 
	 */
    private String assignmentHandlerClassName = null;

    public Participant(String name) {
        this.setName(name);
    }

    public void setAssignmentHandler(String assignmentHandlerClassName) {
        this.assignmentHandlerClassName = assignmentHandlerClassName;
    }

    public String getAssignmentHandler() {
        return assignmentHandlerClassName;
    }
}
