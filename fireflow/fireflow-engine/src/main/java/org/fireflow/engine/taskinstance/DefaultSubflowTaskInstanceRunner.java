/*
 * Copyright 2007-2009 非也
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

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */
package org.fireflow.engine.taskinstance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kernel.KernelException;
import org.fireflow.model.DataField;
import org.fireflow.model.SubflowTask;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.resource.SubWorkflowProcess;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 21, 2009
 */
public class DefaultSubflowTaskInstanceRunner implements ITaskInstanceRunner {

    /* (non-Javadoc)
     * @see org.fireflow.engine.taskinstance.ITaskInstanceRunner#run(org.fireflow.engine.IWorkflowSession, org.fireflow.engine.RuntimeContext, org.fireflow.engine.IProcessInstance, org.fireflow.engine.ITaskInstance)
     */
    public void run(IWorkflowSession currentSession, RuntimeContext runtimeContext, IProcessInstance processInstance,
            ITaskInstance taskInstance) throws EngineException, KernelException {
        if (!Task.SUBFLOW.equals(taskInstance.getTaskType())) {
            throw new EngineException(processInstance,
                    taskInstance.getActivity(),
                    "DefaultSubflowTaskInstanceRunner：TaskInstance的任务类型错误，只能为SUBFLOW类型");
        }
        Task task = taskInstance.getTask();
        SubWorkflowProcess Subflow = ((SubflowTask) task).getSubWorkflowProcess();

        WorkflowDefinition subWorkflowDef = runtimeContext.getDefinitionService().getTheLatestVersionOfWorkflowDefinition(Subflow.getWorkflowProcessId());
        if (subWorkflowDef == null) {
            WorkflowProcess parentWorkflowProcess = taskInstance.getWorkflowProcess();
            throw new EngineException(taskInstance.getProcessInstanceId(), parentWorkflowProcess,
                    taskInstance.getTaskId(),
                    "系统中没有Id为" + Subflow.getWorkflowProcessId() + "的流程定义");
        }
        WorkflowProcess subWorkflowProcess = subWorkflowDef.getWorkflowProcess();

        if (subWorkflowProcess == null) {
            WorkflowProcess parentWorkflowProcess = taskInstance.getWorkflowProcess();
            throw new EngineException(taskInstance.getProcessInstanceId(), parentWorkflowProcess,
                    taskInstance.getTaskId(),
                    "系统中没有Id为" + Subflow.getWorkflowProcessId() + "的流程定义");
        }
        
        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        //更改任务的状态和开始时间
        ((TaskInstance) taskInstance).setState(ITaskInstance.RUNNING);
        ((TaskInstance) taskInstance).setStartedTime(runtimeContext.getCalendarService().getSysDate());
        //TODO wmj2003 应该是update TaskInstance
        persistenceService.saveOrUpdateTaskInstance(taskInstance);


        IProcessInstance subProcessInstance = currentSession.createProcessInstance(subWorkflowProcess.getName(),taskInstance);

        //初始化流程变量,从父实例获得初始值
        Map<String ,Object> processVars = ((TaskInstance) taskInstance).getAliveProcessInstance().getProcessInstanceVariables();
        List<DataField> datafields = subWorkflowProcess.getDataFields();
        for (int i = 0; datafields != null && i < datafields.size(); i++) {
            DataField df = datafields.get(i);
            //TODO wmj2003 疑问，这里的逻辑都不对吧？ 直接 subProcessInstance.setProcessInstanceVariable(df.getName(), processVars.get(df.getName()));
            //还需要判断什么类型啊？反正value是Object
            if (df.getDataType().equals(DataField.STRING)) {
                if (processVars.get(df.getName()) != null && (processVars.get(df.getName()) instanceof String)) {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), processVars.get(df.getName()));
                } else if (df.getInitialValue() != null) {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), df.getInitialValue());
                } else {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), "");
                }
            } else if (df.getDataType().equals(DataField.INTEGER)) {
                if (processVars.get(df.getName()) != null && (processVars.get(df.getName()) instanceof Integer)) {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), processVars.get(df.getName()));
                } else if (df.getInitialValue() != null) {
                    try {
                        Integer intValue = new Integer(df.getInitialValue());
                        subProcessInstance.setProcessInstanceVariable(df.getName(), intValue);
                    } catch (Exception e) {
                    }
                } else {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), new Integer(0));
                }
            } else if (df.getDataType().equals(DataField.FLOAT)) {
                if (processVars.get(df.getName()) != null && (processVars.get(df.getName()) instanceof Float)) {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), processVars.get(df.getName()));
                } else if (df.getInitialValue() != null) {
                    Float floatValue = new Float(df.getInitialValue());
                    subProcessInstance.setProcessInstanceVariable(df.getName(), floatValue);
                } else {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), new Float(0));
                }
            } else if (df.getDataType().equals(DataField.BOOLEAN)) {
                if (processVars.get(df.getName()) != null && (processVars.get(df.getName()) instanceof Boolean)) {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), processVars.get(df.getName()));
                } else if (df.getInitialValue() != null) {
                    Boolean booleanValue = new Boolean(df.getInitialValue());
                    subProcessInstance.setProcessInstanceVariable(df.getName(), booleanValue);
                } else {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), Boolean.FALSE);
                }
            } else if (df.getDataType().equals(DataField.DATETIME)) {
                //TODO 需要完善一下 （ 父子流程数据传递——时间类型的数据还未做传递-不知道为什么？）
            	//wmj2003 20090925 补充上了
                if (processVars.get(df.getName()) != null && (processVars.get(df.getName()) instanceof Date)) {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), processVars.get(df.getName()));
                } else if (df.getInitialValue() != null) {
					try {
	                	SimpleDateFormat dFormat = new SimpleDateFormat(df.getDataPattern());
						Date dateTmp = dFormat.parse(df.getInitialValue());
						subProcessInstance.setProcessInstanceVariable(df.getName(), dateTmp);
					} catch (ParseException e) {
						subProcessInstance.setProcessInstanceVariable(df.getName(), null);
						e.printStackTrace();
					}
                   
                } else {
                    subProcessInstance.setProcessInstanceVariable(df.getName(), null);
                }
            }
        }
        //TODO 应将下面这句删除！这里还需要吗？应该直接subProcessInstance.run()就可以了。
        runtimeContext.getPersistenceService().saveOrUpdateProcessInstance(subProcessInstance);
        subProcessInstance.run();
    }
}
