/**
 * Copyright 2007-2008 陈乜云（非也,Chen Nieyun）
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
package org.fireflow.kernel.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.kernel.ILoopInstance;
import org.fireflow.kernel.INodeInstance;
import org.fireflow.kernel.ITransitionInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.event.INodeInstanceEventListener;
import org.fireflow.kernel.event.NodeInstanceEvent;
import org.fireflow.kernel.plugin.IPlugable;
/**
 * @author chennieyun
 *
 */
public abstract class AbstractNodeInstance implements INodeInstance,IPlugable {

	protected transient List<ITransitionInstance> leavingTransitionInstances = new ArrayList<ITransitionInstance>();
	protected transient List<ITransitionInstance> enteringTransitionInstances = new ArrayList<ITransitionInstance>();
	protected transient List<INodeInstanceEventListener> eventListeners = new ArrayList<INodeInstanceEventListener>();

    protected transient List<ILoopInstance> leavingLoopInstances = new ArrayList<ILoopInstance>();
    protected transient List<ILoopInstance> enteringLoopInstances = new ArrayList<ILoopInstance>();
	/* (non-Javadoc)
	 * @see org.fireflow.kenel.INodeInstance#addLeavingTransitionInstance(org.fireflow.kenel.ITransitionInstance)
	 */
	public void addLeavingTransitionInstance(
			ITransitionInstance transitionInstance) {
		leavingTransitionInstances.add(transitionInstance);

	}

	public List<ITransitionInstance> getLeavingTransitionInstances(){
		return this.leavingTransitionInstances;
	}

	public List<ITransitionInstance> getEnteringTransitionInstances(){
		return this.enteringTransitionInstances;
	}
	public void addEnteringTransitionInstance(ITransitionInstance transitionInstance){
		this.enteringTransitionInstances.add(transitionInstance);
	}

    public void addLeavingLoopInstance(
            ILoopInstance loopInstance) {
        leavingLoopInstances.add(loopInstance);

    }

    public List<ILoopInstance> getLeavingLoopInstances() {
        return this.leavingLoopInstances;
    }

    public List<ILoopInstance> getEnteringLoopInstances() {
        return this.enteringLoopInstances;
    }

    public void addEnteringLoopInstance(ILoopInstance loopInstance) {
        this.enteringLoopInstances.add(loopInstance);
    }
	//TODO 此处是addAll还是直接替换？
	public void setEventListeners(List<INodeInstanceEventListener> listeners){
		eventListeners.addAll(listeners);
	}
	
	public List<INodeInstanceEventListener> getEventListeners(){
		return eventListeners;
	}
		
	/**
	 * wangmj  20090914 增加统一的触发方法，实现类中根据事件的不同而进行触发
	 * @param event
	 * @throws KernelException
	 */
	public void fireNodeEvent(NodeInstanceEvent event) throws KernelException{
		for (int i=0;i<this.eventListeners.size();i++){
			INodeInstanceEventListener listener = this.eventListeners.get(i);
			listener.onNodeInstanceEventFired(event);
		}
	}
}
