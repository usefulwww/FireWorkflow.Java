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
package org.fireflow.engine.kernelextensions;

import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kernel.IActivityInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.event.INodeInstanceEventListener;
import org.fireflow.kernel.event.NodeInstanceEvent;
import org.fireflow.kernel.impl.ActivityInstance;
import org.fireflow.kernel.plugin.IKernelExtension;

/**
 * @author wangmingjie
 *
 */
public class ActivityInstanceExtension implements IKernelExtension,
        INodeInstanceEventListener,IRuntimeContextAware {
    protected RuntimeContext rtCtx = null;
    
    public void setRuntimeContext(RuntimeContext ctx){
        this.rtCtx = ctx;
    }    
    public RuntimeContext getRuntimeContext(){
        return this.rtCtx;
    }    
    public String getExtentionPointName() {
        return ActivityInstance.Extension_Point_NodeInstanceEventListener;
    }

    public String getExtentionTargetName() {
        return ActivityInstance.Extension_Target_Name;
    }

    
    /* (non-Javadoc)
     * @see org.fireflow.kernel.event.INodeInstanceEventListener#onNodeInstanceEventFired(org.fireflow.kernel.event.NodeInstanceEvent)
     */
    public void onNodeInstanceEventFired(NodeInstanceEvent e)
            throws KernelException {
        if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_FIRED) {
            //保存token，并创建taskinstance
            IPersistenceService persistenceService = rtCtx.getPersistenceService();
            //TODO wmj2003 这里是插入还是更新token
            persistenceService.saveOrUpdateToken(e.getToken());
            //触发activity节点，就要创建新的task
            rtCtx.getTaskInstanceManager().createTaskInstances(e.getToken(), (IActivityInstance) e.getSource());
        } else if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_COMPLETED) {
        	//TODO  wmj2003 因为什么原因未处理呢？ 归档任务？
//			RuntimeContext.getInstance()
//			.getTaskInstanceManager()
//			.archiveTaskInstances((IActivityInstance)e.getSource());
        }
    }
}
