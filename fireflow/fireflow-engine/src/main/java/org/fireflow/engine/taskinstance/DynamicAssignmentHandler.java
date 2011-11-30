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

import java.util.List;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.model.FormTask;

/**
 * 动态任务分配句柄，用于指定后续环节的操作员。
 * @author 非也,nychen2000@163.com
 */
public class DynamicAssignmentHandler implements IAssignmentHandler{
	/**
	 * 工作项是否需要签收
	 */
    boolean needClaim = false;
    
    /**
     * 操作员Id列表
     */
    List<String> actorIdsList = null;
    
    public void assign(IAssignable asignable, String performerName) throws EngineException, KernelException {
        if (actorIdsList==null || actorIdsList.size()==0){
            TaskInstance taskInstance = (TaskInstance)asignable;
            throw new EngineException(taskInstance.getProcessInstanceId(),taskInstance.getWorkflowProcess(),
                    taskInstance.getTaskId(),"actorIdsList can not be empty");
        }

        List<IWorkItem> workItems = asignable.assignToActors(actorIdsList);
        
        ITaskInstance taskInst = (ITaskInstance)asignable;
        //如果不需要签收，这里自动进行签收，（FormTask的strategy="all"或者=any并且工作项数量为1）
        if (!needClaim){
	        if (FormTask.ALL.equals(taskInst.getAssignmentStrategy()) ||
	        		(FormTask.ANY.equals(taskInst.getAssignmentStrategy()) && actorIdsList.size()==1)){
	        	for (int i=0;i<workItems.size();i++){
	        		IWorkItem wi = workItems.get(i);
	        		wi.claim();
	        	}
	        }
        }
        	
    }

    public List<String> getActorIdsList() {
        return actorIdsList;
    }

    public void setActorIdsList(List<String> actorIdsList) {
        this.actorIdsList = actorIdsList;
    }

    public boolean isNeedClaim() {
        return needClaim;
    }

    public void setNeedClaim(boolean needSign) {
        this.needClaim = needSign;
    }
    
}
