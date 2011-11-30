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

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.condition.ConditionConstant;
import org.fireflow.kernel.ISynchronizerInstance;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.ITransitionInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.event.INodeInstanceEventListener;
import org.fireflow.kernel.event.NodeInstanceEvent;
import org.fireflow.kernel.plugin.IKernelExtension;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;

/**
 * @author 非也
 * 
 */
public class StartNodeInstance extends AbstractNodeInstance implements
        ISynchronizerInstance {

    public static final String Extension_Target_Name = "org.fireflow.kernel.StartNodeInstance";
    public static List<String> Extension_Point_Names = new ArrayList<String>();
    public static final String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";


    static {
        Extension_Point_Names.add(Extension_Point_NodeInstanceEventListener);
    }
    private int volume = 0;// 即节点的容量

    private StartNode startNode = null;

    public StartNodeInstance(StartNode startNd) {
        this.startNode = startNd;
        volume = startNode.getLeavingTransitions().size();//  start 节点容量 ==输出弧的数量
    }

    public String getId() {
        return this.startNode.getId();
    }

    // 开始节点触发
    /* (non-Javadoc)
     * @see org.fireflow.kernel.INodeInstance#fire(org.fireflow.kernel.IToken)
     */
    public void fire(IToken tk) throws KernelException {
        if (!tk.isAlive()) {//如果token是false，那么直接返回
            return;//
        }
        if (tk.getValue() != volume) {
            KernelException exception = new KernelException(tk.getProcessInstance(),
                    this.startNode,
                    "Error:Illegal StartNodeInstance,the tokenValue MUST be equal to the volume ");
            throw exception;

        }

        tk.setNodeId(this.getSynchronizer().getId());//到开始节点（同步器）

        IProcessInstance processInstance = tk.getProcessInstance();//从token中获得流程实例对象

        //触发token_entered事件
        NodeInstanceEvent event1 = new NodeInstanceEvent(this);
        event1.setToken(tk);
        event1.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED); //token进入
        fireNodeEvent(event1);

        //触发fired事件
        NodeInstanceEvent event2 = new NodeInstanceEvent(this);
        event2.setToken(tk);
        event2.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);//token 触发
        fireNodeEvent(event2);

        //触发leaving事件
        NodeInstanceEvent event4 = new NodeInstanceEvent(this);
        event4.setToken(tk);
        event4.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING); //token 离开
        fireNodeEvent(event4);


        boolean activateDefaultCondition = true;//激活默认弧线的标志
        ITransitionInstance defaultTransInst = null;
        //找到所有开始节点的输出弧 
        for (int i = 0; leavingTransitionInstances != null && i < leavingTransitionInstances.size(); i++) {
            ITransitionInstance transInst = leavingTransitionInstances.get(i); //开始节点的边的类型只能是transition
            String condition = transInst.getTransition().getCondition();
            //如果弧线的条件！=null 并且 =“default” ，那么弧线实例就是default的弧线了。
            if (condition != null && condition.equals(ConditionConstant.DEFAULT)) {
                defaultTransInst = transInst;//记录default转移线，其他条件都未false，才执行它
                continue;
            }

            Token token = new Token(); // 产生新的token
            token.setAlive(true);
            token.setProcessInstance(processInstance);
            token.setFromActivityId(tk.getFromActivityId());
            token.setStepNumber(tk.getStepNumber()+1); //步骤号+1
            
            
            boolean alive = transInst.take(token);//触发弧线的token
            if (alive) {
                activateDefaultCondition = false;
            }

        }
        if (defaultTransInst != null) {//如果defaultTransInst!=null ，走的是default值的弧线 
            Token token = new Token();
            token.setAlive(activateDefaultCondition);//设置token为dead
            token.setProcessInstance(processInstance);
            token.setFromActivityId(token.getFromActivityId());
            token.setStepNumber(tk.getStepNumber()+1);
            defaultTransInst.take(token);
        }


        //触发completed事件
        NodeInstanceEvent event3 = new NodeInstanceEvent(this);
        event3.setToken(tk);
        event3.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED);
        fireNodeEvent(event3);
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
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
                    "Error:When construct the StartNodeInstance,the Extension_Target_Name is mismatching");
        }
        if (Extension_Point_NodeInstanceEventListener.equals(extension.getExtentionPointName())) {
            if (extension instanceof INodeInstanceEventListener) {
                this.eventListeners.add((INodeInstanceEventListener) extension);
            } else {
                throw new RuntimeException(
                        "Error:When construct the StartNodeInstance,the extension MUST be a instance of INodeInstanceEventListener");
            }
        }
    }

    @Override
    public String toString() {
        return "StartNodeInstance_4_[" + startNode.getId() + "]";
    }

    public Synchronizer getSynchronizer() {
        return this.startNode;
    }
}
