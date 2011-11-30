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
package org.fireflow.kernel.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.condition.ConditionConstant;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.kernel.IJoinPoint;
import org.fireflow.kernel.ILoopInstance;
import org.fireflow.kernel.ISynchronizerInstance;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.ITransitionInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.event.INodeInstanceEventListener;
import org.fireflow.kernel.event.NodeInstanceEvent;
import org.fireflow.kernel.plugin.IKernelExtension;
import org.fireflow.model.net.Synchronizer;

/**
 * @author 非也
 * 
 */
public class SynchronizerInstance extends AbstractNodeInstance implements
        ISynchronizerInstance {

    public transient static final Log log = LogFactory.getLog(SynchronizerInstance.class);
    public transient static final String Extension_Target_Name = "org.fireflow.kernel.SynchronizerInstance";
    public transient static List<String> Extension_Point_Names = new ArrayList<String>();
    public transient static final String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";


    static {
        Extension_Point_Names.add(Extension_Point_NodeInstanceEventListener);
    }
    private int volume = 0;// 即节点的容量
    private transient Synchronizer synchronizer = null;


    public String getId() {
        return this.synchronizer.getId();
    }

    public SynchronizerInstance(Synchronizer s) {
        synchronizer = s;
        int a = synchronizer.getEnteringTransitions().size();
        int b = synchronizer.getLeavingTransitions().size();
        volume = a * b;
    }
    /* (non-Javadoc)
     * @see org.fireflow.kernel.INodeInstance#fire(org.fireflow.kernel.IToken)
     */
    public void fire(IToken tk) throws KernelException {
        //TODO 此处性能需要改善一下,20090312
        IJoinPoint joinPoint = null;
        synchronized (this) { //流程同步器需要处理并发的情况，而activity的节点不需要处理并发情况？同步器节点，可能被同时触发，activity节点不会被同时触发？
            tk.setNodeId(this.getSynchronizer().getId());
            log.debug("The weight of the Entering TransitionInstance is " + tk.getValue());
            // 触发TokenEntered事件
            NodeInstanceEvent event1 = new NodeInstanceEvent(this);
            event1.setToken(tk);
            event1.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED);//token 进入
            fireNodeEvent(event1);

            //汇聚检查

            joinPoint = ((ProcessInstance) tk.getProcessInstance()).createJoinPoint(this, tk);// JoinPoint由谁生成比较好？
            int value = joinPoint.getValue();
            log.debug("The volume of " + this.toString() + " is " + volume);
            log.debug("The value of " + this.toString() + " is " + value);
            if (value > volume) {//如果value大于同步器容量，那说明出错了
                KernelException exception = new KernelException(tk.getProcessInstance(),
                        this.getSynchronizer(),
                        "Error:The token count of the synchronizer-instance can NOT be  greater than  it's volumn  ");
                throw exception;
            }
            if (value < volume) {// 如果Value小于容量则继续等待其他弧的汇聚。 (哪些状态为dead的token到此结束，不再向下传递)
                return;  
            }
        }
        //如果汇聚点的容量和同步器节点的容量相同
        IProcessInstance processInstance = tk.getProcessInstance();
        // Synchronize的fire条件应该只与joinPoint的value有关（value==volume），与alive无关
        NodeInstanceEvent event2 = new NodeInstanceEvent(this);
        event2.setToken(tk);
        event2.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);
        fireNodeEvent(event2);

        //在此事件监听器中，删除原有的token
        NodeInstanceEvent event4 = new NodeInstanceEvent(this);
        event4.setToken(tk);
        event4.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
        fireNodeEvent(event4);

        //首先必须检查是否有满足条件的循环，loop比transition有更高的优先级，
        //（只能够有一个loop的条件为true，流程定义的时候需要注意）
        boolean doLoop = false;//表示是否有满足条件的循环，false表示没有，true表示有。
        if (joinPoint.getAlive()) {
            IToken tokenForLoop = new Token(); // 产生新的token
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
        if (!doLoop) {//如果没有循环，则执行transitionInstance
                //非顺序流转的需要生成新的token，
                boolean activiateDefaultCondition = true;
                ITransitionInstance defaultTransInst = null;
                for (int i = 0; leavingTransitionInstances != null && i < leavingTransitionInstances.size(); i++) {
                    ITransitionInstance transInst = leavingTransitionInstances.get(i);
                    String condition = transInst.getTransition().getCondition();
                    if (condition != null && condition.equals(ConditionConstant.DEFAULT)) {
                        defaultTransInst = transInst;
                        continue;
                    }

                    Token token = new Token(); // 产生新的token
                    token.setAlive(joinPoint.getAlive());
                    token.setProcessInstance(processInstance);
                    token.setStepNumber(joinPoint.getStepNumber());
                    token.setFromActivityId(joinPoint.getFromActivityId());
                    boolean alive = transInst.take(token);
                    if (alive) {
                        activiateDefaultCondition = false;
                    }

                }
                if (defaultTransInst != null) {
                    Token token = new Token();
                    token.setAlive(activiateDefaultCondition && joinPoint.getAlive());
                    token.setProcessInstance(processInstance);
                    token.setStepNumber(joinPoint.getStepNumber());
                    token.setFromActivityId(joinPoint.getFromActivityId());  
                    defaultTransInst.take(token);
                }
            
        }

        NodeInstanceEvent event3 = new NodeInstanceEvent(this);
        event3.setToken(tk);
        event3.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED); //触发同步器节点的监听事件，删除所有和同步器节点相关的token
        fireNodeEvent(event3);
    }

    public void setVolume(int arg) {
        volume = arg;
    }

    public int getVolume() {
        return volume;
    }

    public String getExtensionTargetName() {
        return Extension_Target_Name;
    }

    public List<String> getExtensionPointNames() {
        return Extension_Point_Names;
    }

    // TODO extesion是单态还是多实例？单态应该效率高一些。
    public void registExtension(IKernelExtension extension)
            throws RuntimeException {
        if (!Extension_Target_Name.equals(extension.getExtentionTargetName())) {
            throw new RuntimeException(
                    "Error:When construct the SynchronizerInstance,the Extension_Target_Name is mismatching");
        }
        if (Extension_Point_NodeInstanceEventListener.equals(extension.getExtentionPointName())) {
            if (extension instanceof INodeInstanceEventListener) {
                this.eventListeners.add((INodeInstanceEventListener) extension);
            } else {
                throw new RuntimeException(
                        "Error:When construct the SynchronizerInstance,the extension MUST be a instance of INodeInstanceEventListener");
            }
        }
    }

    @Override
    public String toString() {
        return "SynchronizerInstance_4_[" + synchronizer.getId() + "]";
    }

    public Synchronizer getSynchronizer() {
        return this.synchronizer;
    }
}
