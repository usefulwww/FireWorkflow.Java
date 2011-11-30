/**
 * Copyright 2004-2008 非也
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
package org.fireflow.kernel;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.model.IWFElement;

/**
 * wangmj 引擎内核exception定义
 * @author 非也
 *
 */
public class KernelException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7219349319943347690L;
	/**
     * 抛出异常的流程实例的Id
     */
    String processInstanceId = null;
    /**
     * 抛出异常的流程定义的Id
     */
    String processId = null;
    /**
     * 抛出异常的流程的名称
     */
    String processName = null;
    /**
     * 抛出异常的流程的显示名称
     */
    String processDisplayName = null;
    /**
     * 抛出异常的流程元素的Id
     */
    String workflowElementId = null;
    /**
     * 抛出异常的流程元素的名称
     */
    String workflowElementName = null;
    /**
     * 抛出异常的流程元素的显示名称
     */
    String workflowElementDisplayName = null;

    /**
     * 构造方法
     * @param processInstance
     * @param workflowElement
     * @param errMsg
     */
    public KernelException(IProcessInstance processInstance, IWFElement workflowElement, String errMsg) {
        super(errMsg);
        if (processInstance != null) {
            this.setProcessInstanceId(processInstance.getId());
            this.setProcessId(processInstance.getProcessId());
            this.setProcessName(processInstance.getName());
            this.setProcessDisplayName(processInstance.getDisplayName());
        }
        if (workflowElement != null) {
            this.setWorkflowElementId(workflowElement.getId());
            this.setWorkflowElementName(workflowElement.getName());
            this.setWorkflowElementDisplayName(workflowElement.getDisplayName());
        }
    }


    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getWorkflowElementId() {
        return workflowElementId;
    }

    public void setWorkflowElementId(String workflowElementId) {
        this.workflowElementId = workflowElementId;
    }

    public String getWorkflowElementName() {
        return workflowElementName;
    }

    public void setWorkflowElementName(String workflowElementName) {
        this.workflowElementName = workflowElementName;
    }

    public String getProcessDisplayName() {
        return processDisplayName;
    }

    public void setProcessDisplayName(String processDisplayName) {
        this.processDisplayName = processDisplayName;
    }

    public String getWorkflowElementDisplayName() {
        return workflowElementDisplayName;
    }

    public void setWorkflowElementDisplayName(String workflowElementDisplayName) {
        this.workflowElementDisplayName = workflowElementDisplayName;
    }
}
