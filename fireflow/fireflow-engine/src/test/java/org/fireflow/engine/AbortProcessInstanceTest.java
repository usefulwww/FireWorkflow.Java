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
 * @author 非也,lifw555@gmail.com
 * @version 1.0
 * Created on Apr 11, 2009
 */
public class AbortProcessInstanceTest extends FireFlowAbstractTests {

    //--------constant----------------------
    //客户电话，用于控制是否执行“发送手机短信通知客户收货”。通过设置mobile等于null和非null值分别进行测试。
    private final static String mobile = null;//"123123123123";

    //-----variables-----------------
    static String processInstanceId = null;
    static IProcessInstance currentProcessInstance = null;
    static String paymentWorkItemId = null;
    static String prepareGoodsWorkItemId = null;
    static String deliverGoodsWorkItemId = null;
    
    /**
     * 创建流程实例，并执行实例的run方法。
     */
    @Test
    public void testAbortProcessInstance() {
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
        
        this.refresh(currentProcessInstance);
        
        assertNotNull(currentProcessInstance);
        processInstanceId = currentProcessInstance.getId();
        
        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List<IToken> tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        IToken token = (IToken) tokenList.get(0);
        assertEquals(1, token.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PaymentActivity.Payment_Task");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        paymentWorkItemId = ((IWorkItem) workItemList.get(0)).getId();

        //abort processInstance
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                	IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                	currentProcessInstance  = workflowSession.findProcessInstanceById(processInstanceId);
                	String mb = (String)currentProcessInstance.getProcessInstanceVariable("mobile");
                	System.out.println("====the mobile is "+mb);
                    currentProcessInstance.abort();
                } catch (EngineException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        });
        
        this.refresh(currentProcessInstance);
        
        assertEquals(IProcessInstance.CANCELED,currentProcessInstance.getState().intValue());
        
        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "Goods_Deliver_Process.PaymentActivity");
        
        this.refresh(taskInstanceList.get(0));
       
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1, ((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        workItemList = persistenceService.findHaveDoneWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "Goods_Deliver_Process", "Goods_Deliver_Process.PaymentActivity.Payment_Task");

        this.refresh(workItemList.get(0));
        
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.CANCELED), ((IWorkItem) workItemList.get(0)).getState());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(0, tokenList.size());


    }    
}
