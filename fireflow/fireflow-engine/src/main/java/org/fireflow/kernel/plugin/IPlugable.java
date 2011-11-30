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
package org.fireflow.kernel.plugin;

import java.util.List;


/**
 * @author 非也
 *
 */
public interface IPlugable {
	/**
	 * wangmj 获取扩展目标名称
	 * @return
	 */
	public String getExtensionTargetName();
	/**
	 * wangmj获取扩展点名称
	 * @return
	 */
	public List<String> getExtensionPointNames();
	
	//TODO extesion是单态还是多实例？单态应该效率高一些。
	public void registExtension(IKernelExtension extension)throws RuntimeException;
}
