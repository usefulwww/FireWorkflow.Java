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
package org.fireflow.kernel.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.kernel.INodeInstance;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.ITransitionInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.event.EdgeInstanceEvent;
import org.fireflow.kernel.event.IEdgeInstanceEventListener;
import org.fireflow.kernel.plugin.IKernelExtension;
import org.fireflow.kernel.plugin.IPlugable;
import org.fireflow.model.net.Transition;

/**
 * @author 非也
 *
 */
public class TransitionInstance extends EdgeInstance implements ITransitionInstance, IPlugable {

    public transient static final String Extension_Target_Name = "org.fireflow.kernel.TransitionInstance";
    public transient static List<String> Extension_Point_Names = new ArrayList<String>();
    public transient static final String Extension_Point_TransitionInstanceEventListener = "TransitionInstanceEventListener";

    static {
        Extension_Point_Names.add(Extension_Point_TransitionInstanceEventListener);
    }

    private transient Transition transition = null;

    public TransitionInstance(Transition t) {
        transition = t;
    }

    public String getId() {
        return this.transition.getId();
    }

    //获取转移线上的权值
    /* (non-Javadoc)
     * @see org.fireflow.kernel.IEdgeInstance#getWeight()
     */
    public int getWeight() {
        if (weight == 0) {
            if (enteringNodeInstance instanceof StartNodeInstance) {
                weight=1;
                return weight;
                //如果前驱结点是开始节点，那么权值规定为1
            } else if (leavingNodeInstance instanceof EndNodeInstance) {
                weight=1;
                return weight;
                //如果后继结点为结束节点，那么权值规定为1
            } else if (leavingNodeInstance instanceof ActivityInstance) {
                SynchronizerInstance synchronizerInstance = (SynchronizerInstance) enteringNodeInstance;
                int weight = synchronizerInstance.getVolume() / enteringNodeInstance.getLeavingTransitionInstances().size();
                return weight;
                //如果弧线的后继结点 是 task结点，那么弧线的权值=前驱同步器结点的容量/输出弧线的数量
            } else if (leavingNodeInstance instanceof SynchronizerInstance) {
                SynchronizerInstance synchronizerInstance = (SynchronizerInstance) leavingNodeInstance;
                int weight = synchronizerInstance.getVolume() / leavingNodeInstance.getEnteringTransitionInstances().size();
                return weight;
                //如果后继结点是同步器节点，那么权值=同步器的容量/同步器的输入弧线的数量
            }
        }

        return weight;
    }

    /* (non-Javadoc)
     * @see org.fireflow.kernel.IEdgeInstance#take(org.fireflow.kernel.IToken)
     */
    public boolean take(IToken token) throws KernelException {
        EdgeInstanceEvent e = new EdgeInstanceEvent(this);
        e.setToken(token);
        e.setEventType(EdgeInstanceEvent.ON_TAKING_THE_TOKEN);

        for (int i = 0; this.eventListeners != null && i < this.eventListeners.size(); i++) {
            IEdgeInstanceEventListener listener =  this.eventListeners.get(i);
            listener.onEdgeInstanceEventFired(e); //调用TransitionInstanceExtension 来计算弧线上的条件表达式
        }

        INodeInstance nodeInst = this.getLeavingNodeInstance(); //获取到流向哪个节点
        token.setValue(this.getWeight());//获取到弧线上的权值
        boolean alive = token.isAlive();

        nodeInst.fire(token);//节点触发

        return alive;
    }

    public Transition getTransition() {
        return this.transition;
    }

    @SuppressWarnings("static-access")
	public String getExtensionTargetName() {
        return this.Extension_Target_Name;
    }

    @SuppressWarnings("static-access")
	public List<String> getExtensionPointNames() {
        return this.Extension_Point_Names;
    }

    public void registExtension(IKernelExtension extension) throws RuntimeException {
    	if (!Extension_Target_Name.equals(extension.getExtentionTargetName())) {
            return;
    	}
        if (Extension_Point_TransitionInstanceEventListener.equals(extension.getExtentionPointName())) {
            if (extension instanceof IEdgeInstanceEventListener) {
                this.eventListeners.add((IEdgeInstanceEventListener) extension);
            } else {
                throw new RuntimeException("Error:When construct the TransitionInstance,the extension MUST be a instance of ITransitionInstanceEventListener");
            }
        }
    }
}
