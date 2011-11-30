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
package org.fireflow.model;

/**
 * 事件监听器
 * @author 非也,nychen2000@163.com
 */
public class EventListener {
    private String className ;
    
    /**
     * 返回监听器的实现类名称
     * @return 监听器的实现类名称
     */
    public String getClassName() {
        return className;
    }

    /**
     * 设置监听器的实现类名称
     * @param className 监听器的实现类名称
     */
    public void setClassName(String className) {
        this.className = className;
    }
    
}
