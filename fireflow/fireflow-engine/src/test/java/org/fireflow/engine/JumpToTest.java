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

package org.fireflow.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.AssignmentHandlerMock;
import org.fireflow.engine.taskinstance.CurrentUserAssignmentHandlerMock;
import org.fireflow.engine.taskinstance.DynamicAssignmentHandler;
import org.fireflow.engine.test.support.FireFlowAbstractTests;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.KernelException;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 14, 2009
 */
public class JumpToTest extends FireFlowAbstractTests {

    /**
     * 创建流程实例，并执行实例的run方法。
     */
    @Test
    public void testStartNewProcess() {
        System.out.println("--------------Start a new process ----------------");
        IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Jump.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("JumpTo","fireflowTester");

                    processInstance.run();
                    return processInstance;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        //String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
    }

    @Test
    public void testJumpToNextActivity() {
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Jump.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("JumpTo","fireflowTester");

                    processInstance.run();
                    return processInstance;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        final String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
        
    	//--------------------------------------------------------------
        
        System.out.println("--------------Jump To NextActivity ----------------");
        final List actorIds = new ArrayList();
        actorIds.add("Zhang");
        actorIds.add("Li");
        actorIds.add("Chen");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem1 = workflowSession.findWorkItemById(workItem1Id);
                    DynamicAssignmentHandler dynamicAssignmentHandler = new DynamicAssignmentHandler();
                    dynamicAssignmentHandler.setActorIdsList(actorIds);
                    dynamicAssignmentHandler.setNeedClaim(true);
                    workItem1.complete(dynamicAssignmentHandler, "");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(workItem1Id);
        assertEquals(IWorkItem.COMPLETED, wi.getState().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());
        taskInst = (ITaskInstance)taskInstanceList.get(0);
        assertEquals(2,taskInst.getStepNumber().intValue());
        
        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(2,token.getStepNumber().intValue());

        //JumpTo.Activity2.Task1的所有WorkItem
        List<IWorkItem> allTodoWorkItemList4Activity2 = persistenceService.findWorkItemsForTask("JumpTo.Activity2.Task1");
        assertNotNull(allTodoWorkItemList4Activity2);
        assertEquals(3,allTodoWorkItemList4Activity2.size());

        List<IWorkItem> todoWorkItemsList = persistenceService.findTodoWorkItems("Zhang", "JumpTo", "JumpTo.Activity2.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        String workItem2Id = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    }
    
    @Test(expected=RuntimeException.class)
    public void testJumpFromActivity2ToActivity3(){
    	
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Jump.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("JumpTo","fireflowTester");

                    processInstance.run();
                    return processInstance;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        final String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
        
    	//--------------------------------------------------------------
        
        System.out.println("--------------Jump To NextActivity ----------------");
        final List actorIds = new ArrayList();
        actorIds.add("Zhang");
        actorIds.add("Li");
        actorIds.add("Chen");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem1 = workflowSession.findWorkItemById(workItem1Id);
                    DynamicAssignmentHandler dynamicAssignmentHandler = new DynamicAssignmentHandler();
                    dynamicAssignmentHandler.setActorIdsList(actorIds);
                    dynamicAssignmentHandler.setNeedClaim(true);
                    workItem1.complete(dynamicAssignmentHandler, "");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(workItem1Id);
        assertEquals(IWorkItem.COMPLETED, wi.getState().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());
        taskInst = (ITaskInstance)taskInstanceList.get(0);
        assertEquals(2,taskInst.getStepNumber().intValue());
        
        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(2,token.getStepNumber().intValue());

        //JumpTo.Activity2.Task1的所有WorkItem
        List<IWorkItem> allTodoWorkItemList4Activity2 = persistenceService.findWorkItemsForTask("JumpTo.Activity2.Task1");
        assertNotNull(allTodoWorkItemList4Activity2);
        assertEquals(3,allTodoWorkItemList4Activity2.size());

        List<IWorkItem> todoWorkItemsList = persistenceService.findTodoWorkItems("Zhang", "JumpTo", "JumpTo.Activity2.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        final String workItem2Id = ((IWorkItem) todoWorkItemsList.get(0)).getId();
        
    	//-----------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
                    workItem2.claim();
                    workItem2.jumpTo("JumpTo.Activity3");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    @Test
    public void testJumpFromActivity2ToActivity5(){
    	fail("本测试用例没有测试通过，测试用例有问题");
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Jump.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("JumpTo","fireflowTester");

                    processInstance.run();
                    return processInstance;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        final String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
        
    	//--------------------------------------------------------------
        
        System.out.println("--------------Jump To NextActivity ----------------");
        final List actorIds = new ArrayList();
        actorIds.add("Zhang");
        actorIds.add("Li");
        actorIds.add("Chen");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem1 = workflowSession.findWorkItemById(workItem1Id);
                    DynamicAssignmentHandler dynamicAssignmentHandler = new DynamicAssignmentHandler();
                    dynamicAssignmentHandler.setActorIdsList(actorIds);
                    dynamicAssignmentHandler.setNeedClaim(true);
                    workItem1.complete(dynamicAssignmentHandler, "");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(workItem1Id);
        assertEquals(IWorkItem.COMPLETED, wi.getState().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());
        taskInst = (ITaskInstance)taskInstanceList.get(0);
        assertEquals(2,taskInst.getStepNumber().intValue());
        
        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(2,token.getStepNumber().intValue());

        //JumpTo.Activity2.Task1的所有WorkItem
        List<IWorkItem> allTodoWorkItemList4Activity2 = persistenceService.findWorkItemsForTask("JumpTo.Activity2.Task1");
        assertNotNull(allTodoWorkItemList4Activity2);
        assertEquals(3,allTodoWorkItemList4Activity2.size());

        List<IWorkItem> todoWorkItemsList = persistenceService.findTodoWorkItems("Zhang", "JumpTo", "JumpTo.Activity2.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        final String workItem2Id = ((IWorkItem) todoWorkItemsList.get(0)).getId();
        
        this.refresh(((IWorkItem) todoWorkItemsList.get(0)));
        
    	//-----------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
                    workItem2.claim();
                    workItem2.jumpTo("JumpTo.Activity3");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    	//----------------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
                    workItem2.claim();
                    workItem2.jumpTo("JumpTo.Activity5");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(workItem2Id);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(3,token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity5");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());

        todoWorkItemsList = persistenceService.findTodoWorkItems(AssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity5.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        String workItem5Id = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    }

    /**
     * 测试loopStrategy="REDO"
     */
    @Test
    public void testJumpFromActivity5ToActivity2() {
    	fail("本测试用例没有测试通过，测试用例有问题");
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Jump.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("JumpTo","fireflowTester");

                    processInstance.run();
                    return processInstance;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        final String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
        
    	//--------------------------------------------------------------
        
        System.out.println("--------------Jump To NextActivity ----------------");
        final List actorIds = new ArrayList();
        actorIds.add("Zhang");
        actorIds.add("Li");
        actorIds.add("Chen");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem1 = workflowSession.findWorkItemById(workItem1Id);
                    DynamicAssignmentHandler dynamicAssignmentHandler = new DynamicAssignmentHandler();
                    dynamicAssignmentHandler.setActorIdsList(actorIds);
                    dynamicAssignmentHandler.setNeedClaim(true);
                    workItem1.complete(dynamicAssignmentHandler, "");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(workItem1Id);
        assertEquals(IWorkItem.COMPLETED, wi.getState().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());
        taskInst = (ITaskInstance)taskInstanceList.get(0);
        assertEquals(2,taskInst.getStepNumber().intValue());
        
        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(2,token.getStepNumber().intValue());

        //JumpTo.Activity2.Task1的所有WorkItem
        List<IWorkItem> allTodoWorkItemList4Activity2 = persistenceService.findWorkItemsForTask("JumpTo.Activity2.Task1");
        assertNotNull(allTodoWorkItemList4Activity2);
        assertEquals(3,allTodoWorkItemList4Activity2.size());

        List<IWorkItem> todoWorkItemsList = persistenceService.findTodoWorkItems("Zhang", "JumpTo", "JumpTo.Activity2.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        final String workItem2Id = ((IWorkItem) todoWorkItemsList.get(0)).getId();
        
    	//-----------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
                    workItem2.claim();
                    workItem2.jumpTo("JumpTo.Activity3");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    	//----------------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
                    workItem2.claim();
                    workItem2.jumpTo("JumpTo.Activity5");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(workItem2Id);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(3,token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity5");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());

        todoWorkItemsList = persistenceService.findTodoWorkItems(AssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity5.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        final String workItem5Id = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//--------------------------------------------------------------------
    	System.out.println("--------------Complete WorkItem5 ----------------");
        
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem5 = workflowSession.findWorkItemById(workItem5Id);
                    workItem5.claim();
                    workItem5.jumpTo("JumpTo.Activity2");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });



        wi = persistenceService.findWorkItemById(workItem5Id);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(4,token.getStepNumber().intValue());

        List<IWorkItem> workItemsList = persistenceService.findWorkItemsForTask("JumpTo.Activity2.Task1");
        assertNotNull(workItemsList);
        assertEquals(2,workItemsList.size());

        List<IWorkItem> todoWorkItemList = persistenceService.findTodoWorkItems("Zhang");
        assertNotNull(todoWorkItemList);
        assertEquals(1,todoWorkItemList.size());

        //IWorkItem todoWorkItem = (IWorkItem)todoWorkItemList.get(0);
        //workItem2Id = todoWorkItem.getId();


    }

    /**
     * 测试loopstrategy=SKIP
     */
    @Test
    public void testJumpFromActivity2ToActivity5_again() {
    	fail("本测试用例没有测试通过，测试用例有问题");
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Jump.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("JumpTo","fireflowTester");

                    processInstance.run();
                    return processInstance;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        final String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
        
    	//--------------------------------------------------------------
        
        System.out.println("--------------Jump To NextActivity ----------------");
        final List actorIds = new ArrayList();
        actorIds.add("Zhang");
        actorIds.add("Li");
        actorIds.add("Chen");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem1 = workflowSession.findWorkItemById(workItem1Id);
                    DynamicAssignmentHandler dynamicAssignmentHandler = new DynamicAssignmentHandler();
                    dynamicAssignmentHandler.setActorIdsList(actorIds);
                    dynamicAssignmentHandler.setNeedClaim(true);
                    workItem1.complete(dynamicAssignmentHandler, "");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(workItem1Id);
        assertEquals(IWorkItem.COMPLETED, wi.getState().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());
        taskInst = (ITaskInstance)taskInstanceList.get(0);
        assertEquals(2,taskInst.getStepNumber().intValue());
        
        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(2,token.getStepNumber().intValue());

        //JumpTo.Activity2.Task1的所有WorkItem
        List<IWorkItem> allTodoWorkItemList4Activity2 = persistenceService.findWorkItemsForTask("JumpTo.Activity2.Task1");
        assertNotNull(allTodoWorkItemList4Activity2);
        assertEquals(3,allTodoWorkItemList4Activity2.size());

        List<IWorkItem> todoWorkItemsList = persistenceService.findTodoWorkItems("Zhang", "JumpTo", "JumpTo.Activity2.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        final String workItem2Id = ((IWorkItem) todoWorkItemsList.get(0)).getId();
        
    	//-----------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
                    workItem2.claim();
                    workItem2.jumpTo("JumpTo.Activity3");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    	//----------------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
                    workItem2.claim();
                    workItem2.jumpTo("JumpTo.Activity5");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(workItem2Id);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(3,token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity5");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());

        todoWorkItemsList = persistenceService.findTodoWorkItems(AssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity5.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        final String workItem5Id = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//--------------------------------------------------------------------
    	System.out.println("--------------Complete WorkItem5 ----------------");
        
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem5 = workflowSession.findWorkItemById(workItem5Id);
                    workItem5.claim();
                    workItem5.jumpTo("JumpTo.Activity2");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });



        wi = persistenceService.findWorkItemById(workItem5Id);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(4,token.getStepNumber().intValue());

        List<IWorkItem> workItemsList = persistenceService.findWorkItemsForTask("JumpTo.Activity2.Task1");
        assertNotNull(workItemsList);
        assertEquals(2,workItemsList.size());

        List<IWorkItem> todoWorkItemList = persistenceService.findTodoWorkItems("Zhang");
        assertNotNull(todoWorkItemList);
        assertEquals(1,todoWorkItemList.size());

        IWorkItem todoWorkItem = (IWorkItem)todoWorkItemList.get(0);
        final String workItem21Id = todoWorkItem.getId();
    	//----------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem21Id);
                    workItem2.jumpTo("JumpTo.Activity5");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(workItem21Id);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(6,token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity5");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());

        todoWorkItemsList = persistenceService.findTodoWorkItems(AssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity5.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(0, todoWorkItemsList.size());
    }

    /**
     * 测试当前ActivityInstance还不能结束的情况，应该抛出异常
     */
    @Test (expected=RuntimeException.class)
    public void testJumpFromActivity6ToActivity1(){
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Jump.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("JumpTo","fireflowTester");

                    processInstance.run();
                    return processInstance;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        final String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
        
    	//--------------------------------------------------------------
        
        System.out.println("--------------Jump To NextActivity ----------------");
        final List actorIds = new ArrayList();
        actorIds.add("Zhang");
        actorIds.add("Li");
        actorIds.add("Chen");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem1 = workflowSession.findWorkItemById(workItem1Id);
                    DynamicAssignmentHandler dynamicAssignmentHandler = new DynamicAssignmentHandler();
                    dynamicAssignmentHandler.setActorIdsList(actorIds);
                    dynamicAssignmentHandler.setNeedClaim(true);
                    workItem1.complete(dynamicAssignmentHandler, "");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(workItem1Id);
        assertEquals(IWorkItem.COMPLETED, wi.getState().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());
        taskInst = (ITaskInstance)taskInstanceList.get(0);
        assertEquals(2,taskInst.getStepNumber().intValue());
        
        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(2,token.getStepNumber().intValue());

        //JumpTo.Activity2.Task1的所有WorkItem
        List<IWorkItem> allTodoWorkItemList4Activity2 = persistenceService.findWorkItemsForTask("JumpTo.Activity2.Task1");
        assertNotNull(allTodoWorkItemList4Activity2);
        assertEquals(3,allTodoWorkItemList4Activity2.size());

        List<IWorkItem> todoWorkItemsList = persistenceService.findTodoWorkItems("Zhang", "JumpTo", "JumpTo.Activity2.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        final String workItem2Id = ((IWorkItem) todoWorkItemsList.get(0)).getId();
        
    	//-----------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
                    workItem2.claim();
                    workItem2.jumpTo("JumpTo.Activity3");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    	//----------------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
                    workItem2.claim();
                    workItem2.jumpTo("JumpTo.Activity5");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(workItem2Id);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(3,token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity5");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());

        todoWorkItemsList = persistenceService.findTodoWorkItems(AssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity5.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        final String workItem5Id = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//--------------------------------------------------------------------
    	System.out.println("--------------Complete WorkItem5 ----------------");
        
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem5 = workflowSession.findWorkItemById(workItem5Id);
                    workItem5.claim();
                    workItem5.jumpTo("JumpTo.Activity2");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });



        wi = persistenceService.findWorkItemById(workItem5Id);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(4,token.getStepNumber().intValue());

        List<IWorkItem> workItemsList = persistenceService.findWorkItemsForTask("JumpTo.Activity2.Task1");
        assertNotNull(workItemsList);
        assertEquals(2,workItemsList.size());

        List<IWorkItem> todoWorkItemList = persistenceService.findTodoWorkItems("Zhang");
        assertNotNull(todoWorkItemList);
        assertEquals(1,todoWorkItemList.size());

        //IWorkItem todoWorkItem = (IWorkItem)todoWorkItemList.get(0);
        //workItem2Id = todoWorkItem.getId();
    	//----------------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
                    workItem2.jumpTo("JumpTo.Activity5");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(workItem2Id);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        token = (IToken)tokensList.get(0);
        assertEquals(6,token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "JumpTo.Activity5");
        assertNotNull(taskInstanceList);
        assertEquals(1,taskInstanceList.size());

        todoWorkItemsList = persistenceService.findTodoWorkItems(AssignmentHandlerMock.ACTOR_ID, "JumpTo", "JumpTo.Activity5.Task1");
        assertNotNull(todoWorkItemsList);
        assertEquals(0, todoWorkItemsList.size());
    	//---------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    List todoWorkItemsList = workflowSession.findMyTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID);
                    assertNotNull(todoWorkItemsList);
                    assertEquals(2,todoWorkItemsList.size());

                    IWorkItem workItem6_1 = (IWorkItem)todoWorkItemsList.get(0);
                    workItem6_1.claim();
                    workItem6_1.jumpTo("JumpTo.Activity5");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
