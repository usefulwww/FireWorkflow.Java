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
package org.fireflow.engine;

/**
 * org.fireflow.engine.impl.ProcessInstance,org.fireflow.engine.impl.TaskInstance,
 * org.fireflow.engine.impl.WorkItem都实现了该接口。实现该接口的目的是使得对象可以保存和返回当前
 * 的WorkflowSession。<br>
 * 方法IWorkflowSession.execute(IWorkflowSessionCallback callback)会自定判断待返回的对象是否
 * 实现了IWorkflowSessionAware,如果实现该接口，则自动将本身设置该待返回的对象。
 * @author 非也,nychen2000@163.com
 */
public interface IWorkflowSessionAware {
    /**
     * 设置当前的IWorkflowSession
     * @return
     */
    public IWorkflowSession getCurrentWorkflowSession();
    
    /**
     * 返回当前的IWorkflowSession
     * @param session
     */
    public void setCurrentWorkflowSession(IWorkflowSession session);
}
