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
 * 任务引用。用于Activity引用全局的Task。
 * @author 非也
 * @version 1.0
 * Created on Mar 15, 2009
 */
@SuppressWarnings("serial")
public class TaskRef extends AbstractWFElement {
	/**
	 * 被引用的Task
	 */
    Task referencedTask = null;
    
    public TaskRef(IWFElement parent ,Task task){
        super(parent,task.getName());
        referencedTask = task;
    }

    public TaskRef(Task task){
        referencedTask = task;
    }

    public Task getReferencedTask(){
        return referencedTask;
    }

    /*
     * TaskRef的name等于被引用的Task的name
     * (non-Javadoc)
     * @see org.fireflow.model.AbstractWFElement#getName()
     */
    public String getName() {
        return referencedTask.getName();
    }

    public void setName(String name) {

    }

    /**
     * TaskRef的description等于被引用的Task的description
     */
    public String getDescription() {
        return referencedTask.getDescription();
    }

    public void setDescription(String description) {

    }

    @Override
    public String toString() {
        return referencedTask.toString();
    }

    /**
     * TaskRef的显示名等于被引用的Task的显示名
     */
    public String getDisplayName() {
        return referencedTask.getDisplayName();
    }

    public void setDisplayName(String label) {

    }

}
