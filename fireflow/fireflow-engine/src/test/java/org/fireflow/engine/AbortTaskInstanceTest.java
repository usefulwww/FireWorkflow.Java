package org.fireflow.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.AssignToCurrentUserAndCompleteWorkItemHandler;
import org.fireflow.engine.test.support.FireFlowAbstractTests;
import org.fireflow.kernel.KernelException;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public class AbortTaskInstanceTest extends FireFlowAbstractTests {

    //--------constant----------------------
    //客户电话，用于控制是否执行“发送手机短信通知客户收货”。通过设置mobile等于null和非null值分别进行测试。
	//private final static String mobile = null;//"123123123123";

    /**
     * 创建流程实例，并使之执行到B环节。
     */
    @Test
    public void testStartNewProcess() {
        System.out.println("--------------Start a new process ----------------");
        IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Jump.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("AbortTaskInstance",AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID);

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

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "AbortTaskInstance.A");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "AbortTaskInstance", "AbortTaskInstance.A.TaskA");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        final String workItemAId = ((IWorkItem) workItemList.get(0)).getId();
        
        //结束Activity A的工作项，使流程流转到B环节
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem wi = workflowSession.findWorkItemById(workItemAId);
                    wi.claim();
                    wi.complete();
                    return null;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        
        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "AbortTaskInstance.B");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());    
        //String taskInstanceBId = ((ITaskInstance)taskInstanceList.get(0)).getId();
        
        workItemList = persistenceService.findTodoWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "AbortTaskInstance", "AbortTaskInstance.B.TaskB");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        //String workItemBId = ((IWorkItem) workItemList.get(0)).getId();        
    }    
    
    /**
     * 中止B环节，使之流向C环节
     */
    @Test
    public void testAbortActivityB() {
    	
    	IPersistenceService persistenceService = runtimeContext.getPersistenceService();
    	
    	//准备测试数据
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Jump.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("AbortTaskInstance",AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID);

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

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "AbortTaskInstance", "AbortTaskInstance.A.TaskA");
        final String workItemAId = ((IWorkItem) workItemList.get(0)).getId();
        
        //结束Activity A的工作项，使流程流转到B环节
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem wi = workflowSession.findWorkItemById(workItemAId);
                    wi.claim();
                    wi.complete();
                    return null;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });        
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "AbortTaskInstance.B"); 
        final String taskInstanceBId = ((ITaskInstance)taskInstanceList.get(0)).getId();
        
        //如下是测试用例
    	//改变流程变量（即业务条件），使之流向C
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    ITaskInstance taskInstance = workflowSession.findTaskInstanceById(taskInstanceBId);
                    IProcessInstance processInstance = ((TaskInstance) taskInstance).getAliveProcessInstance();
                    processInstance.setProcessInstanceVariable("x", new Integer(3));
                    taskInstance.abort();
                    return null;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });    	

        //List<ITaskInstance> 
        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "AbortTaskInstance.B");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        int taskInstanceState = ((ITaskInstance)taskInstanceList.get(0)).getState();
        assertEquals(ITaskInstance.CANCELED,taskInstanceState);
        
        //List<IWorkItem>
        workItemList = persistenceService.findTodoWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "AbortTaskInstance", "AbortTaskInstance.C.TaskC");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        //String workItemCId = ((IWorkItem) workItemList.get(0)).getId();          
    }
}
