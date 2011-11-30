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
package org.fireflow.engine;

import org.fireflow.engine.beanfactory.IBeanFactory;
import org.fireflow.engine.calendar.ICalendarService;
import org.fireflow.engine.condition.IConditionResolver;
import org.fireflow.engine.definition.IDefinitionService;
import org.fireflow.engine.impl.WorkflowSession;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.ITaskInstanceManager;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.KernelManager;

/**
 * RuntimeContext是Fire workflow Engine的总线。所有的服务都挂接在这个总线上，并通过这个总线获取。<br/>
 * RuntimeContext也是业务代码调用工作流引擎的入口，通过runtimeContext.getWorkflowSession()获得IWorkflowSession 对象，
 * 然后通过IWorkflowSession调用各种工作流实例对象及其API。<br/>
 * 
 * context管理的各种服务
 * @author 非也,nychen2000@163.com
 *
 */
public class RuntimeContext {

    /**
     * 是否已经初始化
     */
    private boolean isInitialized = false;
    
    /**
     * 转移条件表达式解析服务
     */
    private IConditionResolver conditionResolver = null;
    
    /**
     * 实例对象存取服务
     */
    private IPersistenceService persistenceService = null;
    
    /**
     * 流程定义服务，通过该服务获取流程定义
     */
    private IDefinitionService definitionService = null;
    
    /**
     * 内核管理器
     */
    private KernelManager kernelManager = null;
    
    /**
     * TaskInstance 管理器，负责TaskInstance的创建、运行、结束。
     */
    private ITaskInstanceManager taskInstanceManager = null;
    
    /**
     * 日历服务，
     */
    private ICalendarService calendarService = null;
    
    /**
     * bean工厂，fire workflow默认使用spring作为其实现
     */
    private IBeanFactory beanFactory = null;

    /**
     * 是否打开流程跟踪，如果打开，则会往T_FF_HIST_TRACE表中插入纪录。
     */
    private boolean enableTrace = false;
    
    public RuntimeContext() {
    }

    /**
     * 根据bean的name返回bean的实例。<br/>
     * Fire workflow RuntimeContext将该工作委派给org.fireflow.engine.beanfactory.IBeanFatory
     * @param beanName Bean Name具体指什么是由IBeanFatory的实现类来决定的。
     * @return
     */
    public Object getBeanByName(String beanName) {
        if (beanFactory != null) {
            return beanFactory.getBean(beanName);
        } else {
            throw new NullPointerException("The RuntimeContext's beanFactory  can NOT be null");
        }
    }

    /**
     * 返回条件解析器
     * @return
     */
    public IConditionResolver getConditionResolver() {
        return conditionResolver;
    }

    /**
     * 设置条件解析器
     * @param resolver
     */
    public void setConditionResolver(IConditionResolver resolver) {
        this.conditionResolver = resolver;
        if (this.conditionResolver instanceof IRuntimeContextAware) {
            ((IRuntimeContextAware) this.conditionResolver).setRuntimeContext(this);
        }
    }

    /**
     * 返回TaskInstance管理器
     * @return
     */
    public ITaskInstanceManager getTaskInstanceManager() {
        return taskInstanceManager;
    }

    public void setTaskInstanceManager(ITaskInstanceManager taskInstanceManager) {
        this.taskInstanceManager = taskInstanceManager;
        this.taskInstanceManager.setRuntimeContext(this);
    }

    public IPersistenceService getPersistenceService() {
        return this.persistenceService;
    }

    public void setPersistenceService(IPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
        this.persistenceService.setRuntimeContext(this);
    }

    public IDefinitionService getDefinitionService() {
        return definitionService;
    }

    public void setDefinitionService(IDefinitionService service) {
        this.definitionService = service;
        this.definitionService.setRuntimeContext(this);
    }

    public KernelManager getKernelManager() {
        return kernelManager;
    }

    public void setKernelManager(KernelManager arg0) {
        this.kernelManager = arg0;
        this.kernelManager.setRuntimeContext(this);
    }

    public ICalendarService getCalendarService() {
        return calendarService;
    }

    public void setCalendarService(ICalendarService calendarService) {
        this.calendarService = calendarService;
        this.calendarService.setRuntimeContext(this);
    }

    public IWorkflowSession getWorkflowSession() {
        return new WorkflowSession(this);
    }

    public boolean isIsInitialized() {
        return isInitialized;
    }

    public IBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(IBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    /**
     * 初始化方法
     * @throws EngineException
     * @throws KernelException
     */
    public void initialize() throws EngineException, KernelException {
        if (!isInitialized) {
            initAllNetInstances();
            isInitialized = true;
        }
    }

    /**
     * 初始化所有的工作流网实例
     * @throws KernelException
     */
    protected void initAllNetInstances() throws KernelException {

    }

    public boolean isEnableTrace() {
        return enableTrace;
    }

    public void setEnableTrace(boolean enableTrace) {
        this.enableTrace = enableTrace;
    }

}
