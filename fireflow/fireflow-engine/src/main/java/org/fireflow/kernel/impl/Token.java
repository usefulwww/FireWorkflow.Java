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


import java.util.HashMap;
import java.util.Map;

import org.fireflow.engine.EngineConstant;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.kernel.IToken;

/**
 * @author 非也
 *
 */
public class Token implements IToken {

    private Boolean alive = null;
    private Integer value = 0;
    private String nodeId = null;
    private String id = null;
    private String processInstanceId = null;
    private Integer stepNumber = 0;

    private String fromActivityId = null;

    //20090908 
    private transient Map<String ,IProcessInstance> contextInfo = new HashMap<String ,IProcessInstance>();

    /* (non-Javadoc)
     * @see org.fireflow.kenel.IToken#getValue()
     */
    public Integer getValue() {

        return value;
    }

   
    public Boolean isAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    /* (non-Javadoc)
     * @see org.fireflow.kernel.IToken#getProcessInstance()
     */
    public IProcessInstance getProcessInstance() {
    	return (IProcessInstance)this.contextInfo.get(EngineConstant.CURRENT_PROCESS_INSTANCE);
    }

    /* (non-Javadoc)
     * @see org.fireflow.kernel.IToken#setProcessInstance(org.fireflow.engine.IProcessInstance)
     */
    public void setProcessInstance(IProcessInstance inst) {

    	this.contextInfo.put(EngineConstant.CURRENT_PROCESS_INSTANCE, inst);
        if (inst != null) {
            this.processInstanceId = inst.getId();
        } else {
            this.processInstanceId = null;
        }
    }

    public void setValue(Integer v) {
        value = v;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProcessInstanceId(String id) {
        this.processInstanceId = id;
    }

    public String getProcessInstanceId() {
        return this.processInstanceId;
    }

    public Integer getStepNumber(){
        return this.stepNumber;
    }

    public void setStepNumber(Integer i){
        this.stepNumber = i;
    }

    public String getFromActivityId() {
        return this.fromActivityId;
    }

    public void setFromActivityId(String s) {
        this.fromActivityId = s;
    }
    
    public Map<String ,IProcessInstance> getContextInfo(){
    	return this.contextInfo;
    }
}
