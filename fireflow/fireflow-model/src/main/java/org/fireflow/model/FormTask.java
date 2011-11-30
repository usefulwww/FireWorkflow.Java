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

import org.fireflow.model.resource.Form;
import org.fireflow.model.resource.Participant;

/**
 * 表单类型的Task，即人工任务。
 * @author 非也
 * @version 1.0
 * Created on Mar 15, 2009
 */
@SuppressWarnings("serial")
public class FormTask extends Task{
    /**
     * 可编辑表单
     */
    public static final String EDITFORM = "EDITFORM";

    /**
     * 只读表单
     */
    public static final String VIEWFORM = "VIEWFORM";

    /**
     * 列表表单
     */
    public static final String LISTFORM = "LISTFORM";

    /**
     * 任务分配策略之一：ALL。任务分配给角色中的所有人，只有在所有工单结束结束的情况下，任务实例才结束。
     * 用于实现会签。
     */
    public static final String ALL = "ALL";

    /**
     * 任务分配策略之二：ANY。任何一个操作角签收该任务的工单后，其他人的工单被取消掉。
     */
    public static final String ANY = "ANY";

    
    //----------Form Task 的属性
    /**
     * 操作者
     */
    protected Participant performer;//引用participant
    
    /**
     * 该任务的工作项分配策略。取值为FormTask.ANY,FormTask.ALL。
     */
    protected String assignmentStrategy = ANY;//

    /**
     * 缺省表单。
     */
    protected String defaultView = VIEWFORM;//缺省视图是view form

    /**
     * 可编辑表单
     */
    protected Form editForm = null;
    
    /**
     * 只读表单
     */
    protected Form viewForm = null;
    
    /**
     * 列表表单
     */
    protected Form listForm = null;


    public FormTask(){
        this.setType(FORM);
    }

    public FormTask(IWFElement parent, String name) {
        super(parent, name);
        this.setType(FORM);
    } 

    /**
     * 返回任务的操作员。只有FORM类型的任务才有操作员。
     * @return 操作员
     * @see org.fireflow.model.reference.Participant
     */
    public Participant getPerformer() {
        return performer;
    }

    /**
     * 设置任务的操作员
     * @param performer
     * @see org.fireflow.model.reference.Participant
     */
    public void setPerformer(Participant performer) {
        this.performer = performer;
    }

    
    /**
     * 返回任务的分配策略，只对FORM类型的任务有意义。取值为ALL或者ANY
     * @return 任务分配策略值
     */
    public String getAssignmentStrategy() {
        return assignmentStrategy;
    }

    /**
     * 设置任务分配策略，只对FORM类型的任务有意义。取值为ALL或者ANY
     * @param argAssignmentStrategy 任务分配策略值
     */
    public void setAssignmentStrategy(String argAssignmentStrategy) {
        this.assignmentStrategy = argAssignmentStrategy;
    }

    /**
     * 返回任务的缺省表单的类型，取值为EDITFORM、VIEWFORM或者LISTFORM。
     * 只有FORM类型的任务此方法才有意义。该方法的主要作用是方便系统开发，引擎不会用到该方法。
     * @return
     */
    public String getDefaultView() {
        return defaultView;
    }

    public void setDefaultView(String defaultView) {
        this.defaultView = defaultView;
    }

    public Form getEditForm() {
        return editForm;
    }

    public void setEditForm(Form editForm) {
        this.editForm = editForm;
    }

    public Form getViewForm() {
        return viewForm;
    }

    public void setViewForm(Form viewForm) {
        this.viewForm = viewForm;
    }

    public Form getListForm() {
        return listForm;
    }

    public void setListForm(Form listForm) {
        this.listForm = listForm;
    }

}
