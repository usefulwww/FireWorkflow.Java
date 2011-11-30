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
package org.fireflow.engine.taskinstance;

import org.fireflow.engine.EngineException;
import org.fireflow.kernel.KernelException;

/**
 * 任务分配处理程序，工作流系统将真正的任务分配工作交给该处理程序完成。<br>
 * 所有的FORM类型的Task都需要设置其Performer属性，Performer属性实际上是一个Participant对象，
 * 由该对象提供IAssignmentHandler实现类。
 * 
 * @author 非也,nychen2000@163.com
 *
 */
public interface IAssignmentHandler {
    /**
     * 实现任务分配工作，该方法一般的实现逻辑是：<br>
     * 首先根据performerName查询出所有的操作员，可以把performerName当作角色名称。
     * 然后调用asignable.asignToActor(String actorId,boolean needSign)或者
     * asignable.asignToActor(String actorId)或者asignable.asignToActorS(List actorIds)
     * 进行任务分配。
     * @param asignable IAssignable实现类，在FireWorkflow中实际上就是TaskInstance对象。
     * @param performerName 角色名称
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public void assign(IAssignable asignable, String performerName) throws EngineException,KernelException;
    
    //后续版本实现。。。
//    public void assign(IWorkflowSession workflowSession, IProcessInstance processInstance, 
//            IAssignable asignable, String performerName)throws EngineException,KernelException;
}
