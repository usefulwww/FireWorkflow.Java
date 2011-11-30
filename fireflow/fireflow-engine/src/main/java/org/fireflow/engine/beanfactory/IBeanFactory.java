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
package org.fireflow.engine.beanfactory;

/**
 * Engine把创建bean实例的工作委派给该服务。<br/>
 * Engine在如下情况下需要获得相关Bean的实例(未全部枚举)。<br/>
 * 1)Tool类型的Task,Engine通过该服务获得ApplicationHandler的实例然后调用其方法
 * IApplicationHandler.execute(ITaskInstance taskInstace)<br/>
 * 2)Engine在触发事件时，需要获得相关Listener的实例
 * 3)在分配工作项的时候需要获得IAssignmentHandler的实例。
 * @author 非也,nychen2000@163.com
 */
public interface IBeanFactory {
    /**
     * 根据bean的名字返回bean的实例
     * @param beanName bean name具体含义是什么由IBeanFactory的实现类来决定
     * @return
     */
    public Object getBean(String beanName);
}
