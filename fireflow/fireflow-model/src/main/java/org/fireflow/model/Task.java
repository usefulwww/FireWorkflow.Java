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
 * 工作流任务
 * @author 非也,nychen2000@163.com
 */
public abstract class Task extends AbstractWFElement {

	private static final long serialVersionUID = -3497371248100510482L;

	/**
     * 任务类型之二 ：TOOL类型，即工具类型任务，该任务自动调用java代码完成特定的工作。
     */
    public static final String TOOL = "TOOL";
    
    /**
     * 任务类型之三：SUBFLOW类型，即子流程任务
     */
    public static final String SUBFLOW = "SUBFLOW";
    
    /**
     * 任务类型之一：FORM类型，最常见的一类任务，代表该任务需要操作员填写相关的表单。
     */
    public static final String FORM = "FORM";
    
    /**
     * 任务类型之四：DUMMY类型，该类型暂时没有用到，保留。
     */
    public static final String DUMMY = "DUMMY";

    /**
     * 循环情况下，任务分配指示之一：重做<br>
     * 对于Tool类型和Subflow类型的task会重新执行一遍
     * 对于Form类型的Task，重新执行一遍，且将该任务实例分配给最近一次完成同一任务的操作员。
     */
    public static final String REDO = "REDO";

    /**
     * 循环情况下，任务分配指示之二：忽略<br>
     * 循环的情况下该任务将被忽略，即在流程实例的生命周期里，仅执行一遍。
     */
    public static final String SKIP = "SKIP";

    /**
     * 循环的情况下，任务分配指示之三：无<br/>
     * 对于Tool类型和Subflow类型的task会重新执行一遍，和REDO效果一样的。<br/>
     * 对于Form类型的Task，重新执行一遍，且工作流引擎仍然调用Performer属性的AssignmentHandler分配任务
     */
    public static final String NONE = "NONE";
    
    /**
     * 任务类型,取值为FORM,TOOL,SUBFLOW,DUMMY(保留)，缺省值为FORM
     */
    protected String type = FORM;//

    /**
     * 任务执行的时限
     */
    protected Duration duration;


    /**
     * 任务优先级别(1.0暂时没有用到)
     */
    protected int priority = 1;
    
    /**
     * 循环情况下任务执行策略，取值为REDO、SKIP和NONE,
     */
    protected String loopStrategy = REDO;//


    /**
     * 任务实例创建器。如果没有设置，则使用所在流程的全局任务实例创建器。
     */
    protected String taskInstanceCreator = null;

    /**
     * 任务实例运行器，如果没有设置，则使用所在流程的全局的任务实例运行器
     */
    protected String taskInstanceRunner = null;

    /**
     * 任务实例的终结评价器，用于告诉引擎，该实例是否可以结束。如果没有设置，则使用所在流程的全局的任务实例终结评价器。
     */
    protected String taskInstanceCompletionEvaluator = null;
    
    public Task() {
    }

    public Task(IWFElement parent, String name) {
        super(parent, name);
    }
    /**
     * 返回任务的优先级。（引擎暂时没有用到）
     * @return
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 设置任务的优先级。（引擎暂时没有用到）
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String toString() {
        return "Task[id='" + getId() + ", name='" + getName() + "']";
    }

    /**
     * 返回任务的类型
     * @return 任务类型，FORM,TOOL,SUBFLOW或者DUMMY
     */
    public String getType() {
        return type;
    }

    /**
     * 设置任务类型，
     * @param taskType 任务类型，FORM,TOOL,SUBFLOW或者DUMMY
     */
    public void setType(String taskType) {
        this.type = taskType;
    }
    /**
     * 返回任务的完成期限
     * @return 以Duration表示的任务的完成期限
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * 设置任务的完成期限
     * @param limit
     */
    public void setDuration(Duration limit) {
        this.duration = limit;
    }

    public String getTaskInstanceCreator() {
        return taskInstanceCreator;
    }

    public void setTaskInstanceCreator(String taskInstanceCreator) {
        this.taskInstanceCreator = taskInstanceCreator;
    }

    public String getTaskInstanceCompletionEvaluator() {
        return taskInstanceCompletionEvaluator;
    }

    public void setTaskInstanceCompletionEvaluator(String taskInstanceCompletionEvaluator) {
        this.taskInstanceCompletionEvaluator = taskInstanceCompletionEvaluator;
    }


    public String getTaskInstanceRunner() {
        return taskInstanceRunner;
    }

    public void setTaskInstanceRunner(String taskInstanceRunner) {
        this.taskInstanceRunner = taskInstanceRunner;
    }

    public String getLoopStrategy() {
        return loopStrategy;
    }

    public void setLoopStrategy(String loopStrategy) {
        this.loopStrategy = loopStrategy;
    }
}