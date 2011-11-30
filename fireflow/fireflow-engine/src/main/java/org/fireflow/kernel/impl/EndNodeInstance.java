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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.kernel.IJoinPoint;
import org.fireflow.kernel.ILoopInstance;
import org.fireflow.kernel.ISynchronizerInstance;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.event.INodeInstanceEventListener;
import org.fireflow.kernel.event.NodeInstanceEvent;
import org.fireflow.kernel.plugin.IKernelExtension;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Synchronizer;

/**
 * @author 非也,nychen2000@163.com
 * 
 */
public class EndNodeInstance extends AbstractNodeInstance implements
        ISynchronizerInstance {

    public transient static final Log log = LogFactory.getLog(EndNodeInstance.class);
    public static final String Extension_Target_Name = "org.fireflow.kernel.EndNodeInstance";
    public static List<String> Extension_Point_Names = new ArrayList<String>();
    public static final String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";


    static {
        Extension_Point_Names.add(Extension_Point_NodeInstanceEventListener);
    }
    private int volume = 0;// 即节点的容量
    private int tokenValue = 0;
    private EndNode endNode = null;
    @SuppressWarnings("unused")
	private boolean alive = false;

    public EndNodeInstance() {
    }

    public String getId() {
        return this.endNode.getId();
    }

    public EndNodeInstance(EndNode endNd) {
        this.endNode = endNd;
        this.volume = this.endNode.getEnteringTransitions().size();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.fireflow.kenel.ISynchronizerInstance#getTokens()
     */
    public int getValue() {
        return tokenValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.fireflow.kenel.ISynchronizerInstance#getVolume()
     */
    public int getVolume() {
        return volume;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.fireflow.kenel.ISynchronizerInstance#setTokens(int)
     */
    public void setValue(int tokenNum) {
        this.tokenValue = tokenNum;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.fireflow.kenel.ISynchronizerInstance#setVolume(int)
     */
    public void setVolume(int k) {
        this.volume = k;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.fireflow.kenel.IPTNetExecutor#fire(org.fireflow.kenel.RuntimeContext,
     *      org.fireflow.kenel.ITransitionInstance)
     */
    public void fire(IToken tk) throws KernelException {
    	IJoinPoint joinPoint = null;
        synchronized (this) {
            tk.setNodeId(this.getSynchronizer().getId());
            log.debug("The weight of the Entering TransitionInstance is " + tk.getValue());
            // 触发TokenEntered事件
            NodeInstanceEvent event1 = new NodeInstanceEvent(this);
            event1.setToken(tk);
            event1.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED);
            fireNodeEvent(event1);

            //汇聚检查
            joinPoint = ((ProcessInstance) tk.getProcessInstance()).createJoinPoint(this, tk);// JoinPoint由谁生成比较好？
            int value = joinPoint.getValue();

            log.debug("The volume of " + this.toString() + " is " + volume);
            log.debug("The value of " + this.toString() + " is " + value);
            if (value > volume) {
                KernelException exception = new KernelException(tk.getProcessInstance(),
                        this.getSynchronizer(),
                        "Error:The token count of the synchronizer-instance can NOT be  greater than  it's volumn  ");
                throw exception;
            }
            if (value < volume) {// 如果Value小于容量则继续等待其他弧的汇聚。
                return;
            }
        }

        IProcessInstance processInstance = tk.getProcessInstance();
        NodeInstanceEvent event2 = new NodeInstanceEvent(this);
        event2.setToken(tk);
        event2.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);
        fireNodeEvent(event2);
        
        //在此事件监听器中，删除原有的token
        NodeInstanceEvent event4 = new NodeInstanceEvent(this);
        event4.setToken(tk);
        event4.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
        fireNodeEvent(event4);
        
        //首先必须检查是否有满足条件的循环
        boolean doLoop = false;//表示是否有满足条件的循环，false表示没有，true表示有。

        if (joinPoint.getAlive()) {
            IToken tokenForLoop = null;

                tokenForLoop = new Token(); // 产生新的token
                tokenForLoop.setAlive(joinPoint.getAlive());
                tokenForLoop.setProcessInstance(processInstance);
                tokenForLoop.setStepNumber(joinPoint.getStepNumber()-1);
                tokenForLoop.setFromActivityId(joinPoint.getFromActivityId());

            for (int i = 0; i < this.leavingLoopInstances.size(); i++) {
            	
                ILoopInstance loopInstance = this.leavingLoopInstances.get(i);

                doLoop = loopInstance.take(tokenForLoop);
                if (doLoop) {
                    break;
                }
            }
        }

        if (!doLoop){
            NodeInstanceEvent event3 = new NodeInstanceEvent(this);
            event3.setToken(tk);
            event3.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED);
            fireNodeEvent(event3);
        }
        
//        NodeInstanceEvent event4 = new NodeInstanceEvent(this);
//        event4.setToken(tk);
//        event4.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
//        fireNodeLeavingEvent(event4);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.fireflow.kenel.plugin.IPlugable#getExtensionPointNames()
     */
    public List<String> getExtensionPointNames() {
        return Extension_Point_Names;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.fireflow.kenel.plugin.IPlugable#getExtensionTargetName()
     */
    public String getExtensionTargetName() {
        return Extension_Target_Name;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.fireflow.kenel.plugin.IPlugable#registExtension(org.fireflow.kenel.plugin.IKenelExtension)
     */
    public void registExtension(IKernelExtension extension)
            throws RuntimeException {
        if (!Extension_Target_Name.equals(extension.getExtentionTargetName())) {
            throw new RuntimeException(
                    "Error:When construct the EndNodeInstance,the Extension_Target_Name is mismatching");
        }
        if (Extension_Point_NodeInstanceEventListener.equals(extension.getExtentionPointName())) {
            if (extension instanceof INodeInstanceEventListener) {
                this.eventListeners.add((INodeInstanceEventListener) extension);
            } else {
                throw new RuntimeException(
                        "Error:When construct the EndNodeInstance,the extension MUST be a instance of INodeInstanceEventListener");
            }
        }

    }

    @Override
    public String toString() {
        return "EndNodeInstance_4_[" + endNode.getId() + "]";
    }


    public Synchronizer getSynchronizer() {
        return this.endNode;
    }
}
