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

import java.util.Map;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.condition.ConditionConstant;
import org.fireflow.engine.condition.IConditionResolver;
import org.fireflow.engine.impl.ProcessInstanceTrace;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.ITransitionInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.event.EdgeInstanceEvent;
import org.fireflow.kernel.event.IEdgeInstanceEventListener;
import org.fireflow.kernel.impl.TransitionInstance;
import org.fireflow.kernel.plugin.IKernelExtension;
import org.fireflow.model.IWFElement;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.Transition;

/**
 *
 * @author chennieyun
 */
public class TransitionInstanceExtension implements IKernelExtension,
        IEdgeInstanceEventListener, IRuntimeContextAware {

    protected RuntimeContext rtCtx = null;

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
    }

    public RuntimeContext getRuntimeContext() {
        return this.rtCtx;
    }

    /**
     * 执行分支判断策略，即设置token的alive属性 首先，如果this.alive==false,则所有的token的Alive属性皆为false
     * 然后，如果在nexttransitionList中有值，则直接执行列表中的tansition
     * 否则，通过计算Transition的表达式来确定下一个transition,
     * 
     * @param transInst
     * @return
     */
    private boolean determineTheAliveOfToken(Map<String ,Object> vars, String condition) throws Exception{

        // 通过计算transition上的表达式来确定alive的值

        IConditionResolver elResolver = this.rtCtx.getConditionResolver();
        Boolean b = elResolver.resolveBooleanExpression(vars, condition);

        return b;
    }

    /**
     * 计算value值
     * @param token
     * @param condition
     * @throws EngineException
     */
    public void calculateTheAliveValue(IToken token, String condition)throws EngineException {

        if (!token.isAlive()) {
            return;//如果token是dead状态，表明synchronizer的joinpoint是dead状态，不需要重新计算。
        }

        //1、如果没有转移条件，默认为true
        if (condition == null || condition.trim().equals("")) {
            token.setAlive(true);
            return;
        }
        //2、default类型的不需要计算其alive值，该值由synchronizer决定
        if (condition.trim().equals(ConditionConstant.DEFAULT)) {
            return;
        }

        //3、计算EL表达式
        try{
	        boolean alive = determineTheAliveOfToken(token.getProcessInstance().getProcessInstanceVariables(), condition);
	        token.setAlive(alive);
        }catch(Exception ex){
        	throw new EngineException(token.getProcessInstanceId(),token.getProcessInstance().getWorkflowProcess(),token.getNodeId(),ex.getMessage());
        }

    }

    public String getExtentionTargetName() {
        return TransitionInstance.Extension_Target_Name;
    }

    public String getExtentionPointName() {
        return TransitionInstance.Extension_Point_TransitionInstanceEventListener;
    }

    /* (non-Javadoc)
     * @see org.fireflow.kernel.event.IEdgeInstanceEventListener#onEdgeInstanceEventFired(org.fireflow.kernel.event.EdgeInstanceEvent)
     */
    public void onEdgeInstanceEventFired(EdgeInstanceEvent e) throws KernelException {
        if (e.getEventType() == EdgeInstanceEvent.ON_TAKING_THE_TOKEN) {
            IToken token = e.getToken();
            ITransitionInstance transInst = (ITransitionInstance) e.getSource();
            String condition = transInst.getTransition().getCondition();
            
            calculateTheAliveValue(token, condition);

            if (rtCtx.isEnableTrace() && token.isAlive()) {
                Transition transition = transInst.getTransition();
                IWFElement fromNode = transition.getFromNode();
                int minorNumber = 1;
                if (fromNode instanceof Activity){
                    minorNumber=2;
                }else{
                    minorNumber =1;
                }

                ProcessInstanceTrace trace = new ProcessInstanceTrace();
                trace.setProcessInstanceId(e.getToken().getProcessInstanceId());
                trace.setStepNumber(e.getToken().getStepNumber());
                trace.setType(ProcessInstanceTrace.TRANSITION_TYPE);
                trace.setFromNodeId(transInst.getTransition().getFromNode().getId());
                trace.setToNodeId(transInst.getTransition().getToNode().getId());
                trace.setEdgeId(transInst.getTransition().getId());
                trace.setMinorNumber(minorNumber);
                //TODO wmj2003 这里应该是insert。一旦token从当前边上经过，那么就保存流程运行轨迹.
                rtCtx.getPersistenceService().saveOrUpdateProcessInstanceTrace(trace);
            }
        }

    }
}
