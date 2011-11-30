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

package org.fireflow.engine.taskinstance;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.kernel.KernelException;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 21, 2009
 */
public class DefaultSubflowTaskInstanceCompletionEvaluator implements ITaskInstanceCompletionEvaluator{

    /* (non-Javadoc)
     * @see org.fireflow.engine.taskinstance.ITaskInstanceCompletionEvaluator#taskInstanceCanBeCompleted(org.fireflow.engine.IWorkflowSession, org.fireflow.engine.RuntimeContext, org.fireflow.engine.IProcessInstance, org.fireflow.engine.ITaskInstance)
     */
    public boolean taskInstanceCanBeCompleted(IWorkflowSession currentSession,RuntimeContext runtimeContext,
            IProcessInstance processInstance ,ITaskInstance taskInstance)throws EngineException ,KernelException{
        //在Fire Workflow 中，系统默认每个子流程仅创建一个实例，所以当子流程实例完成后，SubflowTaskInstance都可以被completed
        //所以，应该直接返回true;
        return true;

        //如果系统动态创建了多个并发子流程实例，则需要检查是否存在活动的子流程实例，如果存在则返回false，否则返回true。
        //可以用下面的代码实现
//        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
//        Integer count = persistenceService.getAliveProcessInstanceCountForParentTaskInstance(taskInstance.getId());
//        if (count>0){
//            return false;
//        }else{
//            return true;
//        }

    }

}
