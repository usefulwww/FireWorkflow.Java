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
package org.fireflow.kernel.event;

import org.fireflow.kernel.KernelException;
/**
 * @author chennieyun
 *
 */
public interface INodeInstanceEventListener {
	/**
	 * 节点实例监听器
	 * @param e
	 * @throws KernelException
	 */
	public void onNodeInstanceEventFired(NodeInstanceEvent e)throws KernelException;
}
