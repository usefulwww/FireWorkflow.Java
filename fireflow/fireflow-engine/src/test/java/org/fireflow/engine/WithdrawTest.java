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
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

/**
 * 测试撤销功能，流程仍然采用/workflowdefinition/example_workflow.xml
 * @author 非也
 * @version 1.0
 * Created on Mar 14, 2009
 */
public class WithdrawTest extends FireFlowAbstractTests {

    //--------constant----------------------
    //客户电话，用于控制是否执行“发送手机短信通知客户收货”。通过设置mobile等于null和非null值分别进行测试。
    private final static String mobile = "13501527399";

    //-----variables-----------------

    static IProcessInstance currentProcessInstance = null;
    static String paymentWorkItemId = null;
    static String prepareGoodsWorkItemId = null;
    static String deliverGoodsWorkItemId = null;

    /**
     * 创建流程实例，并执行实例的run方法。
     */
    @Test
    public void testStartNewProcess() {
        System.out.println("--------------Start a new process ----------------");
        currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/example_workflow.xml"中的“送货流程”
                    IProcessInstance processInstance = workflowSession.createProcessInstance("Goods_Deliver_Process","fireflowTester");
                    processInstance.setProcessInstanceVariable("mobile", mobile);
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PaymentActivity.Payment_Task");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        paymentWorkItemId = ((IWorkItem) workItemList.get(0)).getId();
    }


    /**
     * 在当前workitem 还处于活动状态时作取回操作，该操作应该不成功。
     */
    @Test(expected=RuntimeException.class)
    public void testWithdrawPaymentWorkItem(){
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/example_workflow.xml"中的“送货流程”
                    IProcessInstance processInstance = workflowSession.createProcessInstance("Goods_Deliver_Process","fireflowTester");
                    processInstance.setProcessInstanceVariable("mobile", mobile);
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PaymentActivity.Payment_Task");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        paymentWorkItemId = ((IWorkItem) workItemList.get(0)).getId();
    	//---------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * 结束"收银岗"的workItem
     */
    @Test
    public void testCompletePaymentWorkItem() {
    	fail("本测试用例没有测试通过，测试用例有问题");
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/example_workflow.xml"中的“送货流程”
                    IProcessInstance processInstance = workflowSession.createProcessInstance("Goods_Deliver_Process","fireflowTester");
                    processInstance.setProcessInstanceVariable("mobile", mobile);
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PaymentActivity.Payment_Task");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        paymentWorkItemId = ((IWorkItem) workItemList.get(0)).getId();
    	//---------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    	//----------------------------------------------------------
        System.out.println("--------------Complete Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        List tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        prepareGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    }

    /**
     * 测试撤销操作，此操作应该成功。
     */
    @Test
    public void testWithdrawPaymentWorkItem2(){
    	fail("本测试用例没有测试通过，测试用例有问题");
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/example_workflow.xml"中的“送货流程”
                    IProcessInstance processInstance = workflowSession.createProcessInstance("Goods_Deliver_Process","fireflowTester");
                    processInstance.setProcessInstanceVariable("mobile", mobile);
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PaymentActivity.Payment_Task");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        paymentWorkItemId = ((IWorkItem) workItemList.get(0)).getId();
    	//---------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    	//----------------------------------------------------------
        System.out.println("--------------Complete Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        List tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        prepareGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//--------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem After It Has Bean Completed----------------");
        IWorkItem newWorkItem = (IWorkItem)transactionTemplate.execute(new TransactionCallback() {


            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    IWorkItem newWorkItem = paymentTaskWorkItem.withdraw();

                    return newWorkItem;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        paymentWorkItemId = newWorkItem.getId();
        
        wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.RUNNING), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List tokensList4Payment = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(tokensList4Payment);
        assertEquals(1, tokensList4Payment.size());
        assertEquals(Boolean.TRUE,((IToken)tokensList4Payment.get(0)).isAlive());
//        assertEquals(((TaskInstance)wi.getTaskInstance()).getTokenId(),((IToken)tokensList4Payment.get(0)).getId());

        List haveDoneWorkItemsList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(haveDoneWorkItemsList);
        assertEquals(1, haveDoneWorkItemsList.size());
        IWorkItem prepareGoodsWorkItem = ((IWorkItem) haveDoneWorkItemsList.get(0));
        prepareGoodsWorkItemId = prepareGoodsWorkItem.getId();
        assertEquals(new Integer(IWorkItem.CANCELED),prepareGoodsWorkItem.getState());
        assertEquals(new Integer(ITaskInstance.CANCELED),prepareGoodsWorkItem.getTaskInstance().getState());
        assertNotNull(prepareGoodsWorkItem.getEndTime());
        assertNotNull(prepareGoodsWorkItem.getTaskInstance().getEndTime());

    }

    /**
     * 撤销操作之后，再次执行 "结束收银岗的workItem"的操作
     */
    @Test
    public void testCompletePaymentWorkItem2() {
    	fail("本测试用例没有测试通过，测试用例有问题");
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/example_workflow.xml"中的“送货流程”
                    IProcessInstance processInstance = workflowSession.createProcessInstance("Goods_Deliver_Process","fireflowTester");
                    processInstance.setProcessInstanceVariable("mobile", mobile);
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PaymentActivity.Payment_Task");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        paymentWorkItemId = ((IWorkItem) workItemList.get(0)).getId();
    	//---------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    	//----------------------------------------------------------
        System.out.println("--------------Complete Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        List tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        prepareGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//--------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem After It Has Bean Completed----------------");
        IWorkItem newWorkItem = (IWorkItem)transactionTemplate.execute(new TransactionCallback() {


            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    IWorkItem newWorkItem = paymentTaskWorkItem.withdraw();

                    return newWorkItem;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        paymentWorkItemId = newWorkItem.getId();
        
        wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.RUNNING), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List tokensList4Payment = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(tokensList4Payment);
        assertEquals(1, tokensList4Payment.size());
        assertEquals(Boolean.TRUE,((IToken)tokensList4Payment.get(0)).isAlive());
//        assertEquals(((TaskInstance)wi.getTaskInstance()).getTokenId(),((IToken)tokensList4Payment.get(0)).getId());

        List haveDoneWorkItemsList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(haveDoneWorkItemsList);
        assertEquals(1, haveDoneWorkItemsList.size());
        IWorkItem prepareGoodsWorkItem = ((IWorkItem) haveDoneWorkItemsList.get(0));
        prepareGoodsWorkItemId = prepareGoodsWorkItem.getId();
        assertEquals(new Integer(IWorkItem.CANCELED),prepareGoodsWorkItem.getState());
        assertEquals(new Integer(ITaskInstance.CANCELED),prepareGoodsWorkItem.getTaskInstance().getState());
        assertNotNull(prepareGoodsWorkItem.getEndTime());
        assertNotNull(prepareGoodsWorkItem.getTaskInstance().getEndTime());
    	//--------------------------------------------------------------
        System.out.println("--------------Complete Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        prepareGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    }

    /**
     * 签收备货工单
     */
    @Test
    public void testClaimPrepareGoodsWorkItem() {
    	fail("本测试用例没有测试通过，测试用例有问题");
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/example_workflow.xml"中的“送货流程”
                    IProcessInstance processInstance = workflowSession.createProcessInstance("Goods_Deliver_Process","fireflowTester");
                    processInstance.setProcessInstanceVariable("mobile", mobile);
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PaymentActivity.Payment_Task");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        paymentWorkItemId = ((IWorkItem) workItemList.get(0)).getId();
    	//---------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    	//----------------------------------------------------------
        System.out.println("--------------Complete Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        List tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        prepareGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//--------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem After It Has Bean Completed----------------");
        IWorkItem newWorkItem = (IWorkItem)transactionTemplate.execute(new TransactionCallback() {


            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    IWorkItem newWorkItem = paymentTaskWorkItem.withdraw();

                    return newWorkItem;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        paymentWorkItemId = newWorkItem.getId();
        
        wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.RUNNING), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List tokensList4Payment = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(tokensList4Payment);
        assertEquals(1, tokensList4Payment.size());
        assertEquals(Boolean.TRUE,((IToken)tokensList4Payment.get(0)).isAlive());
//        assertEquals(((TaskInstance)wi.getTaskInstance()).getTokenId(),((IToken)tokensList4Payment.get(0)).getId());

        List haveDoneWorkItemsList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(haveDoneWorkItemsList);
        assertEquals(1, haveDoneWorkItemsList.size());
        IWorkItem prepareGoodsWorkItem = ((IWorkItem) haveDoneWorkItemsList.get(0));
        prepareGoodsWorkItemId = prepareGoodsWorkItem.getId();
        assertEquals(new Integer(IWorkItem.CANCELED),prepareGoodsWorkItem.getState());
        assertEquals(new Integer(ITaskInstance.CANCELED),prepareGoodsWorkItem.getTaskInstance().getState());
        assertNotNull(prepareGoodsWorkItem.getEndTime());
        assertNotNull(prepareGoodsWorkItem.getTaskInstance().getEndTime());
    	//--------------------------------------------------------------
        System.out.println("--------------Complete Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        prepareGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//---------------------------------------------------------------------
        System.out.println("--------------Claim Prepare Goods WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem prepareGoodsWorkItem = workflowSession.findWorkItemById(prepareGoodsWorkItemId);
                    prepareGoodsWorkItem.claim();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(prepareGoodsWorkItemId);
        assertEquals(new Integer(IWorkItem.RUNNING), wi.getState());
    }

    /**
     * 结束备货工单
     */
    @Test
    public void testCompletePrepareGoodsWorkItem() {
    	fail("本测试用例没有测试通过，测试用例有问题");
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/example_workflow.xml"中的“送货流程”
                    IProcessInstance processInstance = workflowSession.createProcessInstance("Goods_Deliver_Process","fireflowTester");
                    processInstance.setProcessInstanceVariable("mobile", mobile);
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PaymentActivity.Payment_Task");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        paymentWorkItemId = ((IWorkItem) workItemList.get(0)).getId();
    	//---------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    	//----------------------------------------------------------
        System.out.println("--------------Complete Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        List tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        prepareGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//--------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem After It Has Bean Completed----------------");
        IWorkItem newWorkItem = (IWorkItem)transactionTemplate.execute(new TransactionCallback() {


            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    IWorkItem newWorkItem = paymentTaskWorkItem.withdraw();

                    return newWorkItem;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        paymentWorkItemId = newWorkItem.getId();
        
        wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.RUNNING), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List tokensList4Payment = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(tokensList4Payment);
        assertEquals(1, tokensList4Payment.size());
        assertEquals(Boolean.TRUE,((IToken)tokensList4Payment.get(0)).isAlive());
//        assertEquals(((TaskInstance)wi.getTaskInstance()).getTokenId(),((IToken)tokensList4Payment.get(0)).getId());

        List haveDoneWorkItemsList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(haveDoneWorkItemsList);
        assertEquals(1, haveDoneWorkItemsList.size());
        IWorkItem prepareGoodsWorkItem = ((IWorkItem) haveDoneWorkItemsList.get(0));
        prepareGoodsWorkItemId = prepareGoodsWorkItem.getId();
        assertEquals(new Integer(IWorkItem.CANCELED),prepareGoodsWorkItem.getState());
        assertEquals(new Integer(ITaskInstance.CANCELED),prepareGoodsWorkItem.getTaskInstance().getState());
        assertNotNull(prepareGoodsWorkItem.getEndTime());
        assertNotNull(prepareGoodsWorkItem.getTaskInstance().getEndTime());
    	//--------------------------------------------------------------
        System.out.println("--------------Complete Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        prepareGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//---------------------------------------------------------------------
        System.out.println("--------------Claim Prepare Goods WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem prepareGoodsWorkItem = workflowSession.findWorkItemById(prepareGoodsWorkItemId);
                    prepareGoodsWorkItem.claim();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(prepareGoodsWorkItemId);
        assertEquals(new Integer(IWorkItem.RUNNING), wi.getState());
    	//------------------------------------------------------------
        System.out.println("--------------Complete Prepare Goods WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem prepareGoodsWorkItem = workflowSession.findWorkItemById(prepareGoodsWorkItemId);
                    prepareGoodsWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(prepareGoodsWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(2, tokensList.size());

        List tokensListOnEndNode = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.EndNode1");
        assertNotNull(tokensListOnEndNode);
        assertEquals(1, tokensListOnEndNode.size());
        IToken tokenOnEndNode = (IToken)tokensListOnEndNode.get(0);
        if (mobile==null){
            assertEquals(Boolean.FALSE,tokenOnEndNode.isAlive());
        }else{
            assertEquals(Boolean.TRUE,tokenOnEndNode.isAlive());
        }

        todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.DeliverGoodsActivity.DeliverGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        deliverGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    }

    @Test(expected=RuntimeException.class)
    public void testWithdrawPrepareGoodsWorkItem(){
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/example_workflow.xml"中的“送货流程”
                    IProcessInstance processInstance = workflowSession.createProcessInstance("Goods_Deliver_Process","fireflowTester");
                    processInstance.setProcessInstanceVariable("mobile", mobile);
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
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PaymentActivity.Payment_Task");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        paymentWorkItemId = ((IWorkItem) workItemList.get(0)).getId();
    	//---------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.withdraw();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex.getMessage());
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    	//----------------------------------------------------------
        System.out.println("--------------Complete Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        IWorkItem wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        List tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        prepareGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//--------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem After It Has Bean Completed----------------");
        IWorkItem newWorkItem = (IWorkItem)transactionTemplate.execute(new TransactionCallback() {


            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    IWorkItem newWorkItem = paymentTaskWorkItem.withdraw();

                    return newWorkItem;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        paymentWorkItemId = newWorkItem.getId();
        
        wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.RUNNING), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        List tokensList4Payment = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(tokensList4Payment);
        assertEquals(1, tokensList4Payment.size());
        assertEquals(Boolean.TRUE,((IToken)tokensList4Payment.get(0)).isAlive());
//        assertEquals(((TaskInstance)wi.getTaskInstance()).getTokenId(),((IToken)tokensList4Payment.get(0)).getId());

        List haveDoneWorkItemsList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(haveDoneWorkItemsList);
        assertEquals(1, haveDoneWorkItemsList.size());
        IWorkItem prepareGoodsWorkItem = ((IWorkItem) haveDoneWorkItemsList.get(0));
        prepareGoodsWorkItemId = prepareGoodsWorkItem.getId();
        assertEquals(new Integer(IWorkItem.CANCELED),prepareGoodsWorkItem.getState());
        assertEquals(new Integer(ITaskInstance.CANCELED),prepareGoodsWorkItem.getTaskInstance().getState());
        assertNotNull(prepareGoodsWorkItem.getEndTime());
        assertNotNull(prepareGoodsWorkItem.getTaskInstance().getEndTime());
    	//--------------------------------------------------------------
        System.out.println("--------------Complete Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(paymentWorkItemId);
                    paymentTaskWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(paymentWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PrepareGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        prepareGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//---------------------------------------------------------------------
        System.out.println("--------------Claim Prepare Goods WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem prepareGoodsWorkItem = workflowSession.findWorkItemById(prepareGoodsWorkItemId);
                    prepareGoodsWorkItem.claim();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(prepareGoodsWorkItemId);
        assertEquals(new Integer(IWorkItem.RUNNING), wi.getState());
    	//------------------------------------------------------------
        System.out.println("--------------Complete Prepare Goods WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem prepareGoodsWorkItem = workflowSession.findWorkItemById(prepareGoodsWorkItemId);
                    prepareGoodsWorkItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        wi = persistenceService.findWorkItemById(prepareGoodsWorkItemId);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(2, tokensList.size());

        List tokensListOnEndNode = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.EndNode1");
        assertNotNull(tokensListOnEndNode);
        assertEquals(1, tokensListOnEndNode.size());
        IToken tokenOnEndNode = (IToken)tokensListOnEndNode.get(0);
        if (mobile==null){
            assertEquals(Boolean.FALSE,tokenOnEndNode.isAlive());
        }else{
            assertEquals(Boolean.TRUE,tokenOnEndNode.isAlive());
        }

        todoWorkItemsList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.DeliverGoodsActivity.DeliverGoodsTask");
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
        deliverGoodsWorkItemId = ((IWorkItem) todoWorkItemsList.get(0)).getId();
    	//--------------------------------------------------------------------------
        System.out.println("--------------Withdraw Payment WorkItem ----------------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem prepareGoodsWorkItem = workflowSession.findWorkItemById(prepareGoodsWorkItemId);
                    prepareGoodsWorkItem.withdraw();
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
