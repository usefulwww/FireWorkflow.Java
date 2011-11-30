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

import org.fireflow.engine.RuntimeContext;

/**
 * 从关系数据库表T_FF_DF_WORKFLOWDEF中读取流程定义文件，该表保存了同一个流程的各个版本。
 * 该类用于系统的实施阶段。
 * @author 非也,nychen2000@163.com
 */
public class DefinitionService4DBMS implements IDefinitionService {
    protected RuntimeContext rtCtx = null;
    
    
    /* (non-Javadoc)
     * @see org.fireflow.engine.definition.IDefinitionService#getAllLatestVersionsOfWorkflowDefinition()
     */
    public List<WorkflowDefinition> getAllLatestVersionsOfWorkflowDefinition() {
        return rtCtx.getPersistenceService().findAllTheLatestVersionsOfWorkflowDefinition();
        
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.definition.IDefinitionService#getWorkflowDefinitionByProcessIdAndVersionNumber(java.lang.String, java.lang.Integer)
     */
    public WorkflowDefinition getWorkflowDefinitionByProcessIdAndVersionNumber(String id, Integer version) {
        return rtCtx.getPersistenceService().findWorkflowDefinitionByProcessIdAndVersionNumber(id, version);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.definition.IDefinitionService#getTheLatestVersionOfWorkflowDefinition(java.lang.String)
     */
    public WorkflowDefinition getTheLatestVersionOfWorkflowDefinition(String processId) {
        return rtCtx.getPersistenceService().findTheLatestVersionOfWorkflowDefinitionByProcessId(processId);
    }

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
    }
    public RuntimeContext getRuntimeContext(){
        return this.rtCtx;
    } 
}
