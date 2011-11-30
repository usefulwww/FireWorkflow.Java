/**
 * Copyright 2004-2008 陈乜云（非也,Chen Nieyun）
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

import org.fireflow.model.net.Synchronizer;

/**
 * Synchronizer的行为特征是：消耗掉输入的token并产生输出token
 * @author chennieyun
 *
 */
public interface ISynchronizerInstance extends INodeInstance {
	/**
	 * volume是同步器的容量，
	 * @param k
	 */
	public void setVolume(int k);
	/**
	 * @return
	 */
	public int getVolume();

	
	/**
	 * 获取到同步器信息
	 * @return
	 */
	public Synchronizer getSynchronizer();
}
