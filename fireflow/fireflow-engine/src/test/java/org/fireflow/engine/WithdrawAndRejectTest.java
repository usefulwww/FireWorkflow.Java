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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.CurrentUserAssignmentHandlerMock;
import org.fireflow.engine.test.support.FireFlowAbstractTests;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.KernelException;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Apr 11, 2009
 */
public class WithdrawAndRejectTest extends FireFlowAbstractTests {

    //--------constant----------------------
    //本流程用到
    private final static Integer flag = 0;//"123123123123";

    //-----variables-----------------
    static IProcessInstance currentProcessInstance = null;
    static String workItem1Id = null;
    static String workItem2Id = null;
    static String workItem3Id = null;
    static String workItem4Id = null;
    static String workItem5Id = null;
    static String workItem6Id = null;
    static String workItem7Id = null;
    static String workItem8Id = null;
    static String workItem9Id = null;

    @Test
    public void testStartProcessAndCompleteWorkItem1() {
        currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    }

    /**
     * 
     */
    @Test
    public void testWithdrawFromWorkItem1() {
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        
        this.refresh((IWorkItem) workItemList.get(0));
        
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    }

    @Test
    public void testRejectFromWorkItem2() {
    	
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        
        this.refresh((IWorkItem) workItemList.get(0));
        
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(2, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(5, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(6, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(7, token.getStepNumber().intValue());
    }

    @Test(expected = RuntimeException.class)
    public void testWithdrawFromWorkItem1_failed() {
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        
        this.refresh((IWorkItem) taskInstanceList.get(0));
        
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(2, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(5, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(6, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(7, token.getStepNumber().intValue());
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    }

    @Test
    public void testWithdrawFromWorkItem2() {
    	fail("本测试用例没有测试通过，测试用例有问题");
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        
        this.refresh((ITaskInstance) taskInstanceList.get(0));
        
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        
        this.refresh(workItemList.get(0));
        
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(2, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(5, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(6, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(7, token.getStepNumber().intValue());
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(4, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(3, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(8, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(9, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();
    }

    @Test(expected = RuntimeException.class)
    public void testRejectFromWorkItem3_failed() {
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        
        this.refresh((IWorkItem) taskInstanceList.get(0));
        
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(2, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(5, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(6, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(7, token.getStepNumber().intValue());
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(4, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(3, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(8, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(9, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    }

    @Test
    public void testCompleteWorkItem3AndWorkItem4() {
    	fail("本测试用例没有测试通过，测试用例有问题");
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        
        this.refresh((ITaskInstance) taskInstanceList.get(0));
        
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        
        this.refresh(workItemList.get(0));
        
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(2, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(5, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(6, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(7, token.getStepNumber().intValue());
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(4, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(3, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(8, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(9, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();
    	//--------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//-------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.claim();
                    workItem.complete();

                    workItem = workflowSession.findWorkItemById(workItem4Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(10, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity6.Task6");
        assertNotNull(workItemList);
        assertEquals(0, workItemList.size());

    }

    @Test(expected = RuntimeException.class)
    public void testWithdrawFromWorkItem3_failed() {
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        
        this.refresh((IWorkItem) taskInstanceList.get(0));
        
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(2, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(5, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(6, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(7, token.getStepNumber().intValue());
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(4, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(3, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(8, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(9, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();
    	//--------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//-------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.claim();
                    workItem.complete();

                    workItem = workflowSession.findWorkItemById(workItem4Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(10, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity6.Task6");
        assertNotNull(workItemList);
        assertEquals(0, workItemList.size());
    	//---------------------------------------------------------------------------------------------
        System.out.println("------------------withdraw from work item 3--------------------------------");
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    }

    @Test
    public void testRejectFromWorkItem5() {
    	fail("本测试用例没有测试通过，测试用例有问题");
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        
        this.refresh((ITaskInstance) taskInstanceList.get(0));
        
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        
        this.refresh(workItemList.get(0));
        
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(2, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(5, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(6, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(7, token.getStepNumber().intValue());
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(4, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(3, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(8, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(9, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();
    	//--------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//-------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.claim();
                    workItem.complete();

                    workItem = workflowSession.findWorkItemById(workItem4Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(10, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity6.Task6");
        assertNotNull(workItemList);
        assertEquals(0, workItemList.size());
    	//---------------------------------------------------------------------------------------------
        System.out.println("------------------withdraw from work item 3--------------------------------");
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//------------------------------------------------------------------------------
        System.out.println("------------------reject from work item 5--------------------------------");
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());//重点研究。。。
        token = (IToken) tokenList.get(0);
        assertEquals(11, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.complete();

                    workItem = workflowSession.findWorkItemById(workItem4Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(12, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity6.Task6");
        assertNotNull(workItemList);
        assertEquals(0, workItemList.size());

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(13, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity7.Task7");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem7Id = ((IWorkItem) workItemList.get(0)).getId();

    }

    @Test
    public void testWithdrawFromWorkItem5() {
    	fail("本测试用例没有测试通过，测试用例有问题");
       	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        
        this.refresh((ITaskInstance) taskInstanceList.get(0));
        
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        
        this.refresh(workItemList.get(0));
        
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(2, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(5, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(6, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(7, token.getStepNumber().intValue());
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(4, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(3, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(8, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(9, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();
    	//--------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//-------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.claim();
                    workItem.complete();

                    workItem = workflowSession.findWorkItemById(workItem4Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(10, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity6.Task6");
        assertNotNull(workItemList);
        assertEquals(0, workItemList.size());
    	//---------------------------------------------------------------------------------------------
        System.out.println("------------------withdraw from work item 3--------------------------------");
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//------------------------------------------------------------------------------
        System.out.println("------------------reject from work item 5--------------------------------");
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());//重点研究。。。
        token = (IToken) tokenList.get(0);
        assertEquals(11, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.complete();

                    workItem = workflowSession.findWorkItemById(workItem4Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(12, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity6.Task6");
        assertNotNull(workItemList);
        assertEquals(0, workItemList.size());

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(13, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity7.Task7");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem7Id = ((IWorkItem) workItemList.get(0)).getId();

    	//-------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    IWorkItem newWorkItem = workItem.withdraw();
                    workItem5Id = newWorkItem.getId();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(14, token.getStepNumber().intValue());

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(15, token.getStepNumber().intValue());

        List workItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity7.Task7");
        assertNotNull(workItemsList);
        assertEquals(1, workItemsList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemsList.get(0)).getState());

        workItem7Id = ((IWorkItem) workItemsList.get(0)).getId();
    }

    @Test
    public void testRejectFromWorkItem7() {
    	fail("本测试用例没有测试通过，测试用例有问题");
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        
        this.refresh((ITaskInstance) taskInstanceList.get(0));
        
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        
        this.refresh(workItemList.get(0));
        
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(2, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(5, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(6, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(7, token.getStepNumber().intValue());
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(4, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(3, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(8, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(9, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();
    	//--------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//-------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.claim();
                    workItem.complete();

                    workItem = workflowSession.findWorkItemById(workItem4Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(10, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity6.Task6");
        assertNotNull(workItemList);
        assertEquals(0, workItemList.size());
    	//---------------------------------------------------------------------------------------------
        System.out.println("------------------withdraw from work item 3--------------------------------");
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//------------------------------------------------------------------------------
        System.out.println("------------------reject from work item 5--------------------------------");
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());//重点研究。。。
        token = (IToken) tokenList.get(0);
        assertEquals(11, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.complete();

                    workItem = workflowSession.findWorkItemById(workItem4Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(12, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity6.Task6");
        assertNotNull(workItemList);
        assertEquals(0, workItemList.size());

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(13, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity7.Task7");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem7Id = ((IWorkItem) workItemList.get(0)).getId();

    	//-------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    IWorkItem newWorkItem = workItem.withdraw();
                    workItem5Id = newWorkItem.getId();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(14, token.getStepNumber().intValue());

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(15, token.getStepNumber().intValue());

        List workItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity7.Task7");
        assertNotNull(workItemsList);
        assertEquals(1, workItemsList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemsList.get(0)).getState());

        workItem7Id = ((IWorkItem) workItemsList.get(0)).getId();
    	//--------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem7Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(16, token.getStepNumber().intValue());

        workItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemsList);
        assertEquals(1, workItemsList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemsList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemsList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(17, token.getStepNumber().intValue());

        workItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity7.Task7");
        assertNotNull(workItemsList);
        assertEquals(1, workItemsList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemsList.get(0)).getState());

        workItem7Id = ((IWorkItem) workItemsList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem7Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    }

    @Test(expected=RuntimeException.class)
    public void testWithdrawFromWorkItem7_failed() {
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IProcessInstance processInstance = workflowSession.createProcessInstance("WithdrawAndReject","fireflowTester");
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        
        this.refresh((IWorkItem) taskInstanceList.get(0));
        
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.CANCELED), ((IWorkItem) workItemList.get(0)).getState());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(3, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(4, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });


        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(2, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(5, token.getStepNumber().intValue());

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(6, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(7, token.getStepNumber().intValue());
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//--------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "WithdrawAndReject.Activity2");
        assertNotNull(taskInstanceList);
        assertEquals(4, taskInstanceList.size());


        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(3, workItemList.size());


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(8, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem2Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem2Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(9, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();
    	//--------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//-------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.claim();
                    workItem.complete();

                    workItem = workflowSession.findWorkItemById(workItem4Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(10, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity6.Task6");
        assertNotNull(workItemList);
        assertEquals(0, workItemList.size());
    	//---------------------------------------------------------------------------------------------
        System.out.println("------------------withdraw from work item 3--------------------------------");
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//------------------------------------------------------------------------------
        System.out.println("------------------reject from work item 5--------------------------------");
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());//重点研究。。。
        token = (IToken) tokenList.get(0);
        assertEquals(11, token.getStepNumber().intValue());

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem3Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity4.Task4");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItem4Id = ((IWorkItem) workItemList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem3Id);
                    workItem.complete();

                    workItem = workflowSession.findWorkItemById(workItem4Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(12, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemList.get(0)).getId();

        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity6.Task6");
        assertNotNull(workItemList);
        assertEquals(0, workItemList.size());

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(13, token.getStepNumber().intValue());



        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity7.Task7");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItem7Id = ((IWorkItem) workItemList.get(0)).getId();

    	//-------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    IWorkItem newWorkItem = workItem.withdraw();
                    workItem5Id = newWorkItem.getId();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });


        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(14, token.getStepNumber().intValue());

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(15, token.getStepNumber().intValue());

        List workItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity7.Task7");
        assertNotNull(workItemsList);
        assertEquals(1, workItemsList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemsList.get(0)).getState());

        workItem7Id = ((IWorkItem) workItemsList.get(0)).getId();
    	//--------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem7Id);
                    workItem.reject();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(16, token.getStepNumber().intValue());

        workItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity5.Task5");
        assertNotNull(workItemsList);
        assertEquals(1, workItemsList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemsList.get(0)).getState());

        workItem5Id = ((IWorkItem) workItemsList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem5Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        token = (IToken) tokenList.get(0);
        assertEquals(17, token.getStepNumber().intValue());

        workItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "WithdrawAndReject", "WithdrawAndReject.Activity7.Task7");
        assertNotNull(workItemsList);
        assertEquals(1, workItemsList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemsList.get(0)).getState());

        workItem7Id = ((IWorkItem) workItemsList.get(0)).getId();

        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem7Id);
                    workItem.claim();
                    workItem.complete();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    	//-------------------------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem = workflowSession.findWorkItemById(workItem7Id);
                    workItem.withdraw();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                } catch (KernelException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException();
                }

                return null;
            }
        });
    }

}
