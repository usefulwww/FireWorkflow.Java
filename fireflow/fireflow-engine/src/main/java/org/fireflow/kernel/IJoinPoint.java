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
package org.fireflow.kernel;

import org.fireflow.engine.IProcessInstance;

/**
 * wangmj 和T_FF_RT_TOKEN表是对应的。
 * @author chennieyun
 *
 */
public interface IJoinPoint {
	/**
	 * @return
	 */
	public IProcessInstance getProcessInstance();
	/**
	 * @param processInstance
	 */
	public void setProcessInstance(IProcessInstance processInstance);
    /**
     * @return
     */
    public String getProcessInstanceId();
    /**
     * @param id
     */
    public void setProcessInstanceId(String id);
	/**
	 * value是同步器当前的token值之和。
	 * @return
	 */
	public Integer getValue();
	/**
	 * @param v
	 */
	public void addValue(Integer v);
	/**
	 * @return
	 */
	public Boolean getAlive();
	/**
	 * @param alive
	 */
	public void setAlive(Boolean alive);
	/**
	 * @return
	 */
	public String getSynchronizerId() ;

	/**
	 * @param synchronizerId
	 */
	public void setSynchronizerId(String synchronizerId) ;

    /**
     * @return
     */
    public Integer getStepNumber();
    /**
     * @param i
     */
    public void setStepNumber(Integer i);

    /**
     * @return
     */
    public String getFromActivityId();
    /**
     * @param s
     */
    public void setFromActivityId(String s);
}
