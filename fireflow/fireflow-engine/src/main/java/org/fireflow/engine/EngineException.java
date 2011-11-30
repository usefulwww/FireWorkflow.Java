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

import org.fireflow.kernel.KernelException;
import org.fireflow.model.IWFElement;
import org.fireflow.model.WorkflowProcess;

/**
 * wangmj 引擎exception定义
 * @author chennieyun
 *
 */
@SuppressWarnings("serial")
public class EngineException extends KernelException {

	/**
	 * 
	 * @param processInstance 发生异常的流程实例
	 * @param workflowElement 发生异常的流程环节或者Task
	 * @param errMsg 错误信息
	 */
    public EngineException(IProcessInstance processInstance,IWFElement workflowElement,String errMsg){
        super(processInstance,workflowElement,errMsg);
    }

    /**
     * 
     * @param processInstanceId 发生异常的流程实例Id
     * @param process 发生异常的流程
     * @param workflowElementId 发生异常的环节或者Task的Id
     * @param errMsg 错误信息
     */
    public EngineException(String processInstanceId, WorkflowProcess process,
            String workflowElementId, String errMsg) {
        super(null, null, errMsg);
        this.setProcessInstanceId(processInstanceId);
        if (process != null) {
            this.setProcessId(process.getId());
            this.setProcessName(process.getName());
            this.setProcessDisplayName(process.getDisplayName());

            IWFElement workflowElement = process.findWFElementById(workflowElementId);
            if (workflowElement != null) {
                this.setWorkflowElementId(workflowElement.getId());
                this.setWorkflowElementName(workflowElement.getName());
                this.setWorkflowElementDisplayName(workflowElement.getDisplayName());
            }
        }
    }
}
