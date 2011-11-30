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
package org.fireflow.engine.definition;

import java.util.List;

import org.fireflow.engine.IRuntimeContextAware;

/**
 * 流程定义服务。
 * @author 非也，nychen2000@163.com
 *
 */
public interface IDefinitionService extends IRuntimeContextAware {

    /**
     * 返回所有流程的最新版本
     * @return 
     */
    public List<WorkflowDefinition> getAllLatestVersionsOfWorkflowDefinition();
    
    /**
     * 根据流程Id和版本号查找流程定义
     * @param processId
     * @param version
     * @return
     */
    public WorkflowDefinition getWorkflowDefinitionByProcessIdAndVersionNumber(String processId ,Integer version);
    
    /**
     * 通过流程Id查找其最新版本的流程定义
     * @param processId
     * @return
     */
    public WorkflowDefinition getTheLatestVersionOfWorkflowDefinition(String processId);
}
