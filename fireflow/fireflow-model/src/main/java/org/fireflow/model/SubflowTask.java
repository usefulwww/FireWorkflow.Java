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
package org.fireflow.model;

import org.fireflow.model.resource.SubWorkflowProcess;

/**
 * 子流程类型的Task。
 * @author 非也
 * @version 1.0
 * Created on Mar 15, 2009
 */
@SuppressWarnings("serial")
public class SubflowTask extends Task{
	/**
	 * 任务所引用的子流程信息。
	 */
    protected SubWorkflowProcess subWorkflowProcess = null;

    //TODO subflow Task如何会签？

    public SubflowTask(){
        this.setType(SUBFLOW);
    }

    public SubflowTask(IWFElement parent, String name) {
        super(parent, name);
        this.setType(SUBFLOW);
    }

    /**
     * 返回SUBFLOW类型的任务的子流程信息
     * @return
     * @see org.fireflow.model.reference.SubWorkflowProcess
     */
    public SubWorkflowProcess getSubWorkflowProcess() {
        return subWorkflowProcess;
    }

    public void setSubWorkflowProcess(SubWorkflowProcess subWorkflowProcess) {
        this.subWorkflowProcess = subWorkflowProcess;
    }
}
