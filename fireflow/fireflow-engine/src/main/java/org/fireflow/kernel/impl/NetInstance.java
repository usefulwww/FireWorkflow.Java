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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.kernel.INetInstance;
import org.fireflow.kernel.INodeInstance;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.event.INodeInstanceEventListener;
import org.fireflow.kernel.plugin.IKernelExtension;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Loop;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;

/**
 * @author 非也
 *
 */
public class NetInstance implements INetInstance {

    private WorkflowProcess workflowProcess = null;
    private Integer version = null;
    private StartNodeInstance startNodeInstance = null;

    private Map<String, Object> wfElementInstanceMap = new HashMap<String, Object>();
    
    protected List<INodeInstanceEventListener> eventListeners = new ArrayList<INodeInstanceEventListener>();

    /**
     * wangmj  初始化一个工作流网实例,将引擎的扩展属性，注入到对应的工作流元素中
     * @param process
     * @param kenelExtensions
     * @throws KernelException
     */
    public NetInstance(WorkflowProcess process, final Map<String, List<IKernelExtension>> kenelExtensions) throws KernelException{
        this.workflowProcess = process;
        
        //开始节点
        StartNode startNode = workflowProcess.getStartNode();
        startNodeInstance = new StartNodeInstance(startNode);
        List<IKernelExtension> extensionList = kenelExtensions.get(startNodeInstance.getExtensionTargetName());
        for (int i = 0; extensionList != null && i < extensionList.size(); i++) {
            IKernelExtension extension = extensionList.get(i);
            startNodeInstance.registExtension(extension);
        }
        this.setStartNodeInstance(startNodeInstance);
        wfElementInstanceMap.put(startNode.getId(), startNodeInstance);

        //活动节点activity
        List<Activity> activities = workflowProcess.getActivities();
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            ActivityInstance activityInstance = new ActivityInstance(activity);
            extensionList = kenelExtensions.get(activityInstance.getExtensionTargetName());
            for (int j = 0; extensionList != null && j < extensionList.size(); j++) {
                IKernelExtension extension = extensionList.get(j);
                activityInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(activity.getId(), activityInstance);
        }
        //同步器节点
        List<Synchronizer> synchronizers = workflowProcess.getSynchronizers();
        for (int i = 0; i < synchronizers.size(); i++) {
            Synchronizer synchronizer = synchronizers.get(i);
            SynchronizerInstance synchronizerInstance = new SynchronizerInstance(synchronizer);
            extensionList = kenelExtensions.get(synchronizerInstance.getExtensionTargetName());
            for (int j = 0; extensionList != null && j < extensionList.size(); j++) {
                IKernelExtension extension = extensionList.get(j);
                synchronizerInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(synchronizer.getId(), synchronizerInstance);
        }
        //结束节点
        List<EndNode> endNodes = workflowProcess.getEndNodes();

        for (int i = 0; i < endNodes.size(); i++) {
            EndNode endNode = endNodes.get(i);
            EndNodeInstance endNodeInstance = new EndNodeInstance(endNode);

            extensionList = kenelExtensions.get(endNodeInstance.getExtensionTargetName());
            for (int j = 0; extensionList != null && j < extensionList.size(); j++) {
                IKernelExtension extension = extensionList.get(j);
                endNodeInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(endNode.getId(), endNodeInstance);
        }
        //转移线
        List<Transition> transitions = workflowProcess.getTransitions();
        for (int i = 0; i < transitions.size(); i++) {
            Transition transition = transitions.get(i);
            TransitionInstance transitionInstance = new TransitionInstance(transition);

            String fromNodeId = transition.getFromNode().getId();
            if (fromNodeId != null) {
                INodeInstance enteringNodeInstance = (INodeInstance) wfElementInstanceMap.get(fromNodeId);
                if (enteringNodeInstance != null) {
                    enteringNodeInstance.addLeavingTransitionInstance(transitionInstance);
                    transitionInstance.setEnteringNodeInstance(enteringNodeInstance);
                }
            }

            String toNodeId = transition.getToNode().getId();
            if (toNodeId != null) {
                INodeInstance leavingNodeInstance = (INodeInstance) wfElementInstanceMap.get(toNodeId);
                if (leavingNodeInstance != null) {
                    leavingNodeInstance.addEnteringTransitionInstance(transitionInstance);
                    transitionInstance.setLeavingNodeInstance(leavingNodeInstance);
                }
            }
            extensionList = kenelExtensions.get(transitionInstance.getExtensionTargetName());
            for (int j = 0; extensionList != null && j < extensionList.size(); j++) {
                IKernelExtension extension = extensionList.get(j);
                transitionInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(transitionInstance.getId(), transitionInstance);
        }
        //循环线
        List<Loop> loops = workflowProcess.getLoops();
        for (int i = 0; i < loops.size(); i++) {
            Loop loop = loops.get(i);
            LoopInstance loopInstance = new LoopInstance(loop);

            String fromNodeId = loop.getFromNode().getId();
            if (fromNodeId != null) {
                INodeInstance enteringNodeInstance = (INodeInstance) wfElementInstanceMap.get(fromNodeId);
                if (enteringNodeInstance != null) {
                    
                    enteringNodeInstance.addLeavingLoopInstance(loopInstance);
                    loopInstance.setEnteringNodeInstance(enteringNodeInstance);
                }
            }

            String toNodeId = loop.getToNode().getId();
            if (toNodeId != null) {
                INodeInstance leavingNodeInstance = (INodeInstance) wfElementInstanceMap.get(toNodeId);
                if (leavingNodeInstance != null) {
                    leavingNodeInstance.addEnteringLoopInstance(loopInstance);
                    loopInstance.setLeavingNodeInstance(leavingNodeInstance);
                }
            }
            extensionList = kenelExtensions.get(loopInstance.getExtensionTargetName());
            for (int j = 0; extensionList != null && j < extensionList.size(); j++) {
                IKernelExtension extension = extensionList.get(j);
                loopInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(loopInstance.getId(), loopInstance);
        }
    }

    public String getId() {
        return this.workflowProcess.getId();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer v) {
        this.version = v;
    }

    /* (non-Javadoc)
     * @see org.fireflow.kernel.INetInstance#run(org.fireflow.engine.IProcessInstance)
     */
    public void run(IProcessInstance processInstance) throws KernelException {
        if (startNodeInstance == null) {
            KernelException exception = new KernelException(processInstance,
                    this.getWorkflowProcess(),
                    "Error:NetInstance is illegal ，the startNodeInstance can NOT be NULL ");
            throw exception;
        }

        Token token = new Token();//初始化token
        token.setAlive(true); //活动的
        token.setProcessInstance(processInstance); //对应流程实例
        token.setValue(startNodeInstance.getVolume()); //token容量
        token.setStepNumber(0); //步骤号，开始节点的第一步默认为0
        token.setFromActivityId(IToken.FROM_START_NODE); //从哪个节点来 "FROM_START_NODE" 规定的节点。

        //注意这里并没有保存token
        startNodeInstance.fire(token);//启动开始节点 
    }

    /**
     * 结束流程实例，如果流程状态没有达到终态，则直接返回。
     * @throws RuntimeException
     */
    public void complete() throws RuntimeException {
        //1、判断是否所有的EndeNodeInstance都到达终态，如果没有到达，则直接返回。
        //2、执行compelete操作，
        //3、触发after complete事件
        //4、返回主流程
    	
    }

    public WorkflowProcess getWorkflowProcess() {
        return workflowProcess;
    }

    public StartNodeInstance getStartNodeInstance() {
        return startNodeInstance;
    }

    public void setStartNodeInstance(StartNodeInstance startNodeInstance) {
        this.startNodeInstance = startNodeInstance;
    }

    public Object getWFElementInstance(String wfElementId) {
        return wfElementInstanceMap.get(wfElementId);
    }   
}
