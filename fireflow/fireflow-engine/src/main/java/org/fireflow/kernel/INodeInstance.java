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
package org.fireflow.kernel;

import java.util.List;

import org.fireflow.kernel.event.INodeInstanceEventListener;
/**
 * (NodeInstance应该是无状态的，不会随着ProcessInstance的增加而增加。??)
 * @author 非也，nychen2000@163.com
 *
 */
public interface INodeInstance {
	/**
	 * @return
	 */
	public String getId();
	/**
	 * wangmj  node 触发 (最核心的方法)
	 * @param token
	 * @throws KernelException
	 */
	public void fire(IToken token)throws KernelException;
	
	/**
	 * wangmj 获取输出弧的实例
	 * @return
	 */
	public List<ITransitionInstance> getLeavingTransitionInstances();
	/**
	 * @param transitionInstance
	 */
	public void addLeavingTransitionInstance(ITransitionInstance transitionInstance);
	
	/**
	 * @return
	 */
	public List<ITransitionInstance> getEnteringTransitionInstances();
	/**
	 * @param transitionInstance
	 */
	public void addEnteringTransitionInstance(ITransitionInstance transitionInstance);

	/**
	 * @return
	 */
	public List<ILoopInstance> getLeavingLoopInstances();
	/**
	 * @param loopInstance
	 */
	public void addLeavingLoopInstance(ILoopInstance loopInstance);

	/**
	 * @return
	 */
	public List<ILoopInstance> getEnteringLoopInstances();
	/**
	 * @param loopInstance
	 */
	public void addEnteringLoopInstance(ILoopInstance loopInstance);
	
	/**
	 * @param listeners
	 */
	public void setEventListeners(List<INodeInstanceEventListener> listeners);
	
	/**
	 * @return
	 */
	public List<INodeInstanceEventListener> getEventListeners();
	

}
