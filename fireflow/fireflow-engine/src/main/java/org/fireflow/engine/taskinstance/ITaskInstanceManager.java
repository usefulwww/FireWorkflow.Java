/**
 * Copyright 2007-2008 陈乜云（非也,Chen Nieyun）
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
package org.fireflow.engine.taskinstance;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.impl.WorkItem;
import org.fireflow.kernel.IActivityInstance;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.KernelException;

/**
 * 任务实例管理器
 * @author chennieyun
 *
 */
public interface ITaskInstanceManager extends IRuntimeContextAware {

    /**
     * 创建taskinstance实例
     * @param token
     * @param activityInstance
     * @throws EngineException
     */
    public void createTaskInstances(IToken token, IActivityInstance activityInstance) throws EngineException, KernelException;

    /**
     * 将已经完成的taskinstance实例转移到已办表<br>
     * （该方法保留在1.0中未使用，暂时保留，20090317）
     * @param activityInstance
     * @throws EngineException
     */
    public void archiveTaskInstances(IActivityInstance activityInstance) throws EngineException, KernelException;

    /**
     * 启动TaskInstance，其状态将从INITIALIZED变成STARTED状态。<br>
     * 对于Tool类型的TaskInstance,将直接调用外部应用程序。<br>
     * 对于Sbuflow类型的TaskInstance，将启动子流程。<br>
     * 对于Form类型的TaskInstance，仅改变其状态纪录启动时间。<br>
     * @param taskInstance
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public void startTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance,
            ITaskInstance taskInstance) throws EngineException, KernelException   ;

    /**
     * 结束TaskInstance以及当前的ActivityInstance，并执行targetActivityInstance环节实例。<br>
     * 如果targetActivityInstance为null表示由工作流引擎根据流程定义自动流转到下一个环节。
     * @param taskInstance
     * @param targetActivityInstance
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public void completeTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance,
            ITaskInstance taskInstance, IActivityInstance targetActivityInstance) throws EngineException, KernelException;


    /**
     * 中止task instance。
     * @param currentSession
     * @param processInstance
     * @param taskInstance
     * @param targetActivityInstance
     * @throws EngineException
     * @throws KernelException
     */
    public void abortTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance,
            ITaskInstance taskInstance, String targetActivityId) throws EngineException, KernelException ;
    
    /**
     * 中止task instance。
     * @param currentSession
     * @param processInstance
     * @param taskInstance
     * @param targetActivityInstance
     * @throws EngineException
     * @throws KernelException
     */
    public void abortTaskInstanceEx(IWorkflowSession currentSession, IProcessInstance processInstance,
            ITaskInstance taskInstance, String targetActivityId) throws EngineException, KernelException ;
    
    
    /**
     * 根据TaskInstance创建workItem。
     * @param taskInstance
     * @param actorId
     * @return
     * @throws org.fireflow.engine.EngineException
     */
    public WorkItem createWorkItem(IWorkflowSession currentSession, IProcessInstance processInstance,ITaskInstance taskInstance, String actorId) throws EngineException;

    /**
     * 签收WorkItem。
     * @param workItem
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public IWorkItem claimWorkItem(String workItemId,String taskInstanceId)throws EngineException, KernelException ;

    /**
     * 结束WorkItem
     * @param workItem
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kernel.KernelException
     */
    public void completeWorkItem(IWorkItem workItem,IActivityInstance targetActivityInstance,String comments)throws EngineException, KernelException ;

    /**
     * 结束工单并跳转
     * @param workItem
     * @param targetActivityId
     * @param nextActorIds
     * @param needClaim
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kernel.KernelException
     */
    public void completeWorkItemAndJumpTo(IWorkItem workItem,String targetActivityId, String comments) throws EngineException, KernelException ;

    /**
     * 结束工单并跳转（超级）
     * @param workItem
     * @param targetActivityId
     * @param comments
     * @throws EngineException
     * @throws KernelException
     */
    public void completeWorkItemAndJumpToEx(IWorkItem workItem,String targetActivityId, String comments) throws EngineException, KernelException ;

    /**
     * 撤销刚才执行的Complete动作，系统将创建并返回一个新的Running状态的WorkItem
     * @param workItem
     * @return 新创建的工作项
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kernel.KernelException
     */
    public IWorkItem withdrawWorkItem(IWorkItem workItem) throws EngineException, KernelException ;

    /**
     * 拒收
     * @param workItem
     * @param comments
     * @throws EngineException
     * @throws KernelException
     */
    public void rejectWorkItem(IWorkItem workItem,String comments) throws  EngineException, KernelException ;

    /**
     * 将工作项位派给其他人，自己的工作项变成CANCELED状态。返回新创建的WorkItem.
     * @param workItem 我的WorkItem
     * @param actorId 被委派的Actor的Id
     * @param comments 备注信息
     * @return 新创建的工作项
     */
    public IWorkItem reassignWorkItemTo(IWorkItem workItem,String actorId,String comments);
}
