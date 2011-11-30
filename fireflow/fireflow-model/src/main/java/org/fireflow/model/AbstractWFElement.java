/**
 * Copyright 2003-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程元素抽象类
 * @author 非也,nychen2000@163.com
 *
 */
public abstract class AbstractWFElement implements IWFElement, Serializable {

	private static final long serialVersionUID = -320956235993400548L;

	/**
	 * 元素序列号，请不要在业务代码里面使用该属性的信息。因为这个属性的值是变化的。
	 */
    private String sn = null;
    
    /**
     * 父元素
     */
    private IWFElement parentElement;
    
    /**
     * 名称
     */
    private String name;
    
    /**
     * 显示名称
     */
    private String displayName;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 事件监听器
     */
    private List<EventListener> eventListeners = new ArrayList<EventListener>();   
    
    /**
     * 扩展属性
     */
    private Map<String, String> extendedAttributes;

    /**
     * 构造方法
     */
    public AbstractWFElement(){
        
    }
    
    /**
     * 
     * @param parentElement 父流程元素
     * @param name 本流程元素的名称
     */
    public AbstractWFElement(IWFElement parentElement, String name) {
        this.parentElement = parentElement;
        setName(name);
    }

    public String getId() {
        if (parentElement == null) {
            return this.getName();
        } else {
            return parentElement.getId() + "." + this.getName();
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getExtendedAttributes() {
        if (extendedAttributes == null) {
            extendedAttributes = new HashMap<String, String>();
        }
        return extendedAttributes;
    }
    public List<EventListener> getEventListeners(){
        return this.eventListeners;
    }
    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof IWFElement) &&
                this.getId().equals(((AbstractWFElement) obj).getId()));
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public String toString() {
        return (displayName == null || displayName.trim().equals("")) ? this.getName() : displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String label) {
        this.displayName = label;
    }

    public IWFElement getParent() {
        return parentElement;
    }

    public void setParent(IWFElement parentElement) {
        this.parentElement = parentElement;
    }

    public String getSn(){
        return sn;
    }

    public void setSn(String s){
        sn = s;
    }
}
