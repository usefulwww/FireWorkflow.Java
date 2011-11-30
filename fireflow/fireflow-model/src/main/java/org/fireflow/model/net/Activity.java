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
package org.fireflow.model.net;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.Task;
import org.fireflow.model.TaskRef;
import org.fireflow.model.WorkflowProcess;

/**
 * 环节。
 * @author 非也,nychen2000@163.com
 *
 */
@SuppressWarnings("serial")
public class Activity extends Node {
	/**
	 * 环节实例结束策略之一：他的所有的任务实例结束后才可以结束。
	 */
    public static final String ALL = "ALL";
    
    /**
     * 环节实例结束策略之二：任何一个Task实例结束后环节实例可以结束。
     */
    public static final String ANY = "ANY";
    
    /**
     * 输入转移
     */
    private Transition enteringTransition;//输入弧 TODO 可以有多个输入弧2010-01-17
    
    /**
     * 输出转移
     */
    private Transition leavingTransition;//输出弧

    /**
     * 对全局Task的引用的列表
     */
    private List<TaskRef> taskRefs = new ArrayList<TaskRef>();
    
    /**
     * 局部的task列表
     */
    private List<Task> inlineTasks = new ArrayList<Task>();
    
    /**
     * 环节实例结束策略，缺省为ALL
     */
    private String completionStrategy = ALL;

    public Activity(){
        
    }
    public Activity(WorkflowProcess workflowProcess, String name) {
        super(workflowProcess, name);
    }

    public List<Task> getInlineTasks(){
        return this.inlineTasks;
    }
    
    public List<TaskRef> getTaskRefs(){
        return taskRefs;
    }

    /**
     * 返回该环节所有的Task。<br>
     * 这些Task是inlineTask列表和taskRef列表解析后的所有的Task的和。
     * @return
     */
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<Task>();
        tasks.addAll(this.inlineTasks);
        for (int i=0;i<this.taskRefs.size();i++){
            TaskRef taskRef = taskRefs.get(i);
            tasks.add(taskRef.getReferencedTask());
        }
        return tasks;
    }

    /**
     * 返回环节的结束策略，取值为ALL或者ANY，缺省值为ALL<br>
     * 如果取值为ALL,则只有其所有任务实例结束了，环节实例才可以结束。<br>
     * 如果取值为ANY，则只要任何一个任务实例结束后，环节实例就可以结束。
     * 环节实例的结束操作仅执行一遍，因此后续任务实例的结束不会触发环节实例的结束操作再次执行。
     * @return
     */
    public String getCompletionStrategy() {
        return completionStrategy;
    }

    public void setCompletionStrategy(String strategy) {
        this.completionStrategy = strategy;
    }

    /**
     * 返回环节的输入Transition。一个环节有且只有一个输入Transition
     * @return 转移
     */
    public Transition getEnteringTransition() {
        return enteringTransition;
    }

    public void setEnteringTransition(Transition enteringTransition) {
        this.enteringTransition = enteringTransition;

    }

    /**
     * 返回环节的输出Transition。一个环节有且只有一个输出Transition
     * @return
     */
    public Transition getLeavingTransition() {
        return leavingTransition;
    }

    public void setLeavingTransition(Transition leavingTransition) {
        this.leavingTransition = leavingTransition;
    }

}
