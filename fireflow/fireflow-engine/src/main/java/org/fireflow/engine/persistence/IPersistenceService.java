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
package org.fireflow.engine.persistence;

import java.util.List;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.impl.ProcessInstanceTrace;
import org.fireflow.engine.impl.ProcessInstanceVar;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.kernel.IToken;

/**
 * 数据存储接口，<br>
 * (目前该接口的方法还不够，下一步增加方法，把hibernate的QBC和QBE直接集成进来。)<br/>
 * 约定：以下划线开头的方法只提供给引擎本身使用，这些方法都经过特定优化。
 * 例如增加分区查询条件。
 * @author 非也,nychen2000@163.com
 *
 */
public interface IPersistenceService extends IRuntimeContextAware{
    /******************************************************************************/
    /************                                                        **********/
    /************            Process instance 相关的持久化方法            **********/    
    /************            Persistence methods for process instance    **********/
    /************                                                        **********/    
    /******************************************************************************/
    /**
     * 插入或者更新ProcessInstance 。同时也会保存或更新流程变量<br/>
     * Save or update processinstance. 
     * If the processInstance.id is null then insert a new process instance record
     * and genarate a new id for it (save operation);
     * otherwise update the existent one.
     * 
     * @param processInstance
     */
    public void saveOrUpdateProcessInstance(IProcessInstance processInstance);
    
    /**
     * 通过ID获得“活的”ProcessInstance对象。<br>
     * “活的”是指ProcessInstance.state=INITIALIZED Or ProcessInstance.state=STARTED Or ProcessInstance=SUSPENDED的流程实例
     * @param id processInstance.id
     * @return process instance
     */
    public IProcessInstance findAliveProcessInstanceById(String id);


    /**
     * 通过ID获得ProcessInstance对象。
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param id processInstance.id
     * @return process instance
     */
    public IProcessInstance findProcessInstanceById(String id);


    /**
     * 查找并返回同一个业务流程的所有实例
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param processId The id of the process definition.
     * @return A list of processInstance
     */
    public List<IProcessInstance> findProcessInstancesByProcessId(String processId);
    

    /**
     * 查找并返回同一个指定版本业务流程的所有实例
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param processId The id of the process definition.
     * @return A list of processInstance
     */
    public List<IProcessInstance> findProcessInstancesByProcessIdAndVersion(String processId,Integer version);

    /**
     * 计算活动的子流程实例的数量
     * @param taskInstanceId 父TaskInstance的Id
     * @return
     */
    public Integer getAliveProcessInstanceCountForParentTaskInstance(String taskInstanceId);


    /**
     * 终止流程实例。将流程实例、活动的TaskInstance、活动的WorkItem的状态设置为CANCELED；并删除所有的token
     * @param processInstanceId
     */
    public void abortProcessInstance(ProcessInstance processInstance);

    /**
     * 挂起流程实例
     * @param processInstance
     */
    public void suspendProcessInstance(ProcessInstance processInstance);

    /**
     * 恢复流程实例
     * @param processInstance
     */
    public void restoreProcessInstance(ProcessInstance processInstance);

    
    /******************************************************************************/
    /************                                                        **********/
    /************            与流程变量相关的持久化方法                    **********/    
    /************            Persistence methods for process instance    **********/
    /************                                                        **********/    
    /******************************************************************************/    
    
    /**
     * 查询流程实例的所有变量
     * @param processInstanceId 流程实例的Id
     * @return 流程实例的所有变量
     */
    public List<ProcessInstanceVar> findProcessInstanceVariable(String processInstanceId);
    
    /**
     * 
     * @param processInstanceId
     * @param name
     * @return
     */
    public ProcessInstanceVar findProcessInstanceVariable(String processInstanceId,String name);
    
    /**
     * @param var
     */
    public void updateProcessInstanceVariable(ProcessInstanceVar var);
    
    /**
     * @param var
     */
    public void saveProcessInstanceVariable(ProcessInstanceVar var);
    
    /******************************************************************************/
    /************                                                        **********/
    /************            task instance 相关的持久化方法               **********/    
    /************            Persistence methods for task instance       **********/
    /************                                                        **********/    
    /******************************************************************************/
    /**
     * 插入或者更新TaskInstance。<br/>
     * Save or update task instance. If the taskInstance.id is null then insert a new task instance record
     * and generate a new id for it ;
     * otherwise update the existent one. 
     * @param taskInstance
     */
    public void saveOrUpdateTaskInstance(ITaskInstance taskInstance);

    /**
     * 终止TaskInstance。将任务实例及其所有的“活的”WorkItem变成Canceled状态。<br/>
     * "活的"WorkItem 是指状态等于INITIALIZED、STARTED或者SUSPENDED的WorkItem.
     * @param taskInstanceId
     */
    public void abortTaskInstance(TaskInstance taskInstance);
    
    /**
     * 返回“活的”TaskInstance。<br/>
     * “活的”是指TaskInstance.state=INITIALIZED Or TaskInstance.state=STARTED 。
     * @param id
     * @return
     */
    public ITaskInstance findAliveTaskInstanceById(String id);

    /**
     * 获得activity的“活的”TaskInstance的数量<br/>
     * “活的”是指TaskInstance.state=INITIALIZED Or TaskInstance.state=STARTED 。
     * @param processInstanceId 流程实例ID
     * @param activityId 任务ID
     * @return
     */
    public Integer getAliveTaskInstanceCountForActivity(String processInstanceId, String activityId);

    /**
     * 返回某个Task已经结束的TaskInstance的数量。<br/>
     * “已经结束”是指TaskInstance.state=COMPLETED。
     * @param processInstanceId
     * @param taskId
     * @return
     */
    public Integer getCompletedTaskInstanceCountForTask(String processInstanceId,String taskId);


    /**
     * Find the task instance by id
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param id
     * @return
     */
    public ITaskInstance findTaskInstanceById(String id);
    
    /**
     * 查询流程实例的所有的TaskInstance,如果activityId不为空，则返回该流程实例下指定环节的TaskInstance<br/>
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param processInstanceId  the id of the process instance
     * @param activityId  if the activityId is null, then return all the taskinstance of the processinstance;
     * @return
     */
    public List<ITaskInstance> findTaskInstancesForProcessInstance(String processInstanceId, String activityId);


    /**
     * 查询出同一个stepNumber的所有TaskInstance实例
     * @param processInstanceId
     * @param stepNumber
     * @return
     */
    public List<ITaskInstance> findTaskInstancesForProcessInstanceByStepNumber(String processInstanceId,Integer stepNumber);
    
    
    /**
     * 调用数据库自身的机制所定TaskInstance实例。<br/>
     * 该方法主要用于工单的签收操作，在签收之前先锁定与之对应的TaskInstance。
     * @param taskInstanceId
     * @return
     */
    public void lockTaskInstance(String taskInstanceId);
    
    
    /******************************************************************************/
    /************                                                        **********/
    /************            workItem 相关的持久化方法                    **********/    
    /************            Persistence methods for workitem            **********/
    /************                                                        **********/    
    /******************************************************************************/
    /**
     * 插入或者更新WorkItem<br/>
     * save or update workitem
     * @param workitem
     */
    public void saveOrUpdateWorkItem(IWorkItem workitem);




    /**
     * 返回任务实例的所有"活的"WorkItem的数量。<br>
     * "活的"WorkItem 是指状态等于INITIALIZED、STARTED或者SUSPENDED的WorkItem。
     * @param taskInstanceId
     * @return
     */
    public Integer getAliveWorkItemCountForTaskInstance(String taskInstanceId);

    /**
     * 查询任务实例的所有"已经结束"WorkItem。<br>
     * 
     * 所以必须有关联条件WorkItem.state=IWorkItem.COMPLTED 
     *
     * @param taskInstanceId 任务实例Id
     * @return
     */
    public List<IWorkItem> findCompletedWorkItemsForTaskInstance(String taskInstanceId);

    /**
     * 查询某任务实例的所有WorkItem
     * @param taskInstanceId
     * @return
     */
    public List<IWorkItem> findWorkItemsForTaskInstance(String taskInstanceId);
    
    
    /**
     * 删除处于初始化状态的workitem。
     * 此方法用于签收Workitem时，删除其他Actor的WorkItem
     * @param taskInstanceId
     */
    public void deleteWorkItemsInInitializedState(String taskInstanceId);

    
    /**
     * Find workItem by id
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param id
     * @return
     */
    public IWorkItem findWorkItemById(String id);
    
    
    /**
     *
     * Find all workitems for task
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param taskid
     * @return
     */
    public List<IWorkItem> findWorkItemsForTask(String taskid);    
    

    /**
     * 根据操作员的Id返回其待办工单。如果actorId==null，则返回系统所有的待办任务<br/>
     * 待办工单是指状态等于INITIALIZED或STARTED工单<br/>
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param actorId
     * @return
     */
    public List<IWorkItem> findTodoWorkItems(String actorId);
    
    /**
     * 查找操作员在某个流程实例中的待办工单。
     * 如果processInstanceId为空，则等价于调用findTodoWorkItems(String actorId)
     * 待办工单是指状态等于INITIALIZED或STARTED工单<br/>
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param actorId
     * @param processInstanceId
     * @return
     */
    public List<IWorkItem> findTodoWorkItems(String actorId,String processInstanceId);
    
    /**
     * 查找操作员在某个流程某个任务上的待办工单。
     * actorId，processId，taskId都可以为空（null或者""）,为空的条件将被忽略
     * 待办工单是指状态等于INITIALIZED或STARTED工单<br/>
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param actorId
     * @param processId
     * @param taskId
     * @return
     */
    public List<IWorkItem> findTodoWorkItems(String actorId,String processId,String taskId);
    
    /**
     * 根据操作员的Id返回其已办工单。如果actorId==null，则返回系统所有的已办任务
     * 已办工单是指状态等于COMPLETED或CANCELED的工单<br/>
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param actorId
     * @return
     */
    public List<IWorkItem> findHaveDoneWorkItems(String actorId);
    
    /**
     * 查找操作员在某个流程实例中的已办工单。
     * 如果processInstanceId为空，则等价于调用findHaveDoneWorkItems(String actorId)
     * 已办工单是指状态等于COMPLETED或CANCELED的工单<br/>
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param actorId
     * @param processInstanceId
     * @return
     */
    public List<IWorkItem> findHaveDoneWorkItems(String actorId,String processInstanceId);
    
    /**
     * 查找操作员在某个流程某个任务上的已办工单。
     * actorId，processId，taskId都可以为空（null或者""）,为空的条件将被忽略
     * 已办工单是指状态等于COMPLETED或CANCELED的工单<br/>
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param actorId
     * @param processId
     * @param taskId
     * @return
     */
    public List<IWorkItem> findHaveDoneWorkItems(String actorId,String processId,String taskId);
        


    /******************************************************************************/
    /************                                                        **********/
    /************            token 相关的持久化方法                       **********/    
    /************            Persistence methods for token               **********/
    /************                                                        **********/    
    /******************************************************************************/
    /**
     * Save token
     * @param token
     */
    public void saveOrUpdateToken(IToken token);

    /**
     * 统计流程任意节点的活动Token的数量。对于Activity节点，该数量只能取值1或者0，大于1表明有流程实例出现异常。
     * @param processInstanceId
     * @param nodeId
     * @return
     */
    public Integer getAliveTokenCountForNode(String processInstanceId, String nodeId);


    /**
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param id
     * @return
     */
    public IToken findTokenById(String id);

    /**
     * Find all the tokens for process instance ,and the nodeId of the token must equals to the second argument.
     * @param processInstanceId the id of the process instance
     * @param nodeId if the nodeId is null ,then return all the tokens of the process instance.
     * @return
     */
    public List<IToken> findTokensForProcessInstance(String processInstanceId, String nodeId);

    /**
     * 删除某个节点的所有token
     * @param processInstanceId
     * @param nodeId
     */
    public void deleteTokensForNode(String processInstanceId,String nodeId);

    /**
     * 删除某些节点的所有token
     * @param processInstanceId
     * @param nodeIdsList
     */
    public void deleteTokensForNodes(String processInstanceId,List<String> nodeIdsList);

    /**
     * 删除token
     * @param token
     */
    public void deleteToken(IToken token);

    /******************************************************************************/
    /************                                                        **********/
    /************            存取流程定义文件 相关的持久化方法             **********/    
    /************            Persistence methods for workflow definition **********/
    /************                                                        **********/    
    /******************************************************************************/
    /**
     * Save or update the workflow definition. The version will be increased automatically when insert a new record.<br>
     * 保存流程定义，如果同一个ProcessId的流程定义已经存在，则版本号自动加1。
     * @param workflowDef
     */
    public void saveOrUpdateWorkflowDefinition(WorkflowDefinition workflowDef);
     
    /**
     * Find the workflow definition by id .
     * 根据纪录的ID返回流程定义
     * @param id
     * @return
     */
    public WorkflowDefinition findWorkflowDefinitionById(String id);
    
    /**
     * Find workflow definition by workflow process id and version<br>
     * 根据ProcessId和版本号返回流程定义
     * @param processId
     * @param version
     * @return
     */
    public WorkflowDefinition findWorkflowDefinitionByProcessIdAndVersionNumber(String processId,int version);
    
    /**
     * Find the latest version of the workflow definition.<br>
     * 根据processId返回最新版本的有效流程定义
     * @param processId the workflow process id 
     * @return
     */
    public WorkflowDefinition findTheLatestVersionOfWorkflowDefinitionByProcessId(String processId);
            
    /**
     * Find all the workflow definitions for the workflow process id.<br>
     * 根据ProcessId 返回所有版本的流程定义
     * @param processId
     * @return
     */
    public List<WorkflowDefinition> findWorkflowDefinitionsByProcessId(String processId);

    /**
     * Find all of the latest version of workflow definitions.<br>
     * 返回系统中所有的最新版本的有效流程定义
     * @return
     */
    public List<WorkflowDefinition> findAllTheLatestVersionsOfWorkflowDefinition();
    
    /**
     * Find the latest version number <br>
     * 返回最新的有效版本号 （只是针对已经发布的版本）
     * @param processId
     * @return the version number ,null if there is no workflow definition stored in the DB.
     */
    public Integer findTheLatestVersionNumber(String processId);
    
    /**
     * 返回最新版本号(忽略是否发布)
     * @param processId
     * @return
     */
    public Integer findTheLatestVersionNumberIgnoreState(String processId);
    
    

    /********************************process instance trace info **********************/
    public void saveOrUpdateProcessInstanceTrace(ProcessInstanceTrace processInstanceTrace);
    /**
     * 20090923 modified by wmj2003
     * 根据流程实例ID查找流程实例运行轨迹
     * @param processInstanceId 流程实例ID
     * @return
     */
    public List<ProcessInstanceTrace> findProcessInstanceTraces(String processInstanceId);
    
    /******************************** lifw555@gmail.com **********************/
    
    /**
     * 获得操作员所要操作工单的总数量
     * publishUser如果为null，获取全部
     * @param actorId 操作员主键 
     * @param publishUser 流程定义发布者
     * @return
     * @author lifw555@gmail.com
     * @throws RuntimeException
     */
    public Integer getTodoWorkItemsCount(String actorId,String publishUser) throws RuntimeException;

    /**
     * 获得操作员所要操作工单列表（分页）
     * publishUser如果为null，获取全部
     * @param actorId 操作员主键
     * @param publishUser 流程定义发布者
     * @param pageSize 每页显示的条数
     * @param pageNumber 当前页数
     * @return
     * @author lifw555@gmail.com
     * @throws RuntimeException
     */
	public List<IWorkItem> findTodoWorkItems(String actorId,String publishUser,int pageSize,int pageNumber) throws RuntimeException;

	/**
	 * 获得操作员完成的工单总数量
	 * publishUser如果为null，获取全部
	 * @param actorId 操作员主键 
	 * @param publishUser 流程定义发布者
	 * @return
	 * @author lifw555@gmail.com
	 * @throws RuntimeException
	 */
	public Integer getHaveDoneWorkItemsCount(String actorId,String publishUser) throws RuntimeException;

	/**
	 * 获得操作员完成的工单列表（分页）
	 * publishUser如果为null，获取全部
	 * @param actorId 操作员主键
	 * @param publishUser 流程定义发布者
	 * @param pageSize 每页显示的条数
	 * @param pageNumber 当前页数
	 * @return
	 * @author lifw555@gmail.com
	 * @throws RuntimeException
	 */
	public List<IWorkItem> findHaveDoneWorkItems(String actorId,String publishUser,int pageSize,int pageNumber) throws RuntimeException;
	
	/**
	 * 获得操作员发起的工作流实例总数量
	 * publishUser如果为null，获取全部
	 * @param actorId 操作员主键
	 * @param publishUser 流程定义发布者
	 * @return
	 * @author lifw555@gmail.com
	 * @throws RuntimeException
	 */
	public Integer getProcessInstanceCountByCreatorId(String creatorId,String publishUser) throws RuntimeException;
	
	/**
	 * 获得操作员发起的工作流实例列表（分页）
	 * publishUser如果为null，获取全部
	 * @param actorId 操作员主键
	 * @param publishUser 流程定义发布者
	 * @param pageSize 每页显示的条数
	 * @param pageNumber 当前页数
	 * @return
	 * @author lifw555@gmail.com
	 * @throws RuntimeException
	 */
	public List<IProcessInstance> findProcessInstanceListByCreatorId(String creatorId,String publishUser,int pageSize,int pageNumber) throws RuntimeException; 

	/**
	 * 获得工作流发布者发起的所有流程定义的工作流实例总数量
	 * @param publishUser 工作流发布者
	 * @return
	 * @author lifw555@gmail.com
	 * @throws RuntimeException
	 */
	public Integer getProcessInstanceCountByPublishUser(String publishUser) throws RuntimeException;
	
	/**
	 * 获得工作流发布者发起的所有流程定义的工作流实例列表（分页）
	 * @param publishUser 工作流发布者
	 * @param pageSize 每页显示的条数
	 * @param pageNumber 当前页数
	 * @return
	 * @author lifw555@gmail.com
	 * @throws RuntimeException
	 */
	public List<IProcessInstance> findProcessInstanceListByPublishUser(String publishUser,int pageSize,int pageNumber) throws RuntimeException;
	
}
