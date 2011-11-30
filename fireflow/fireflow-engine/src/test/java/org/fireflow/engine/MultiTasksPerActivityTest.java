package org.fireflow.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

public class MultiTasksPerActivityTest extends FireFlowAbstractTests {

    //-----variables-----------------
    static IProcessInstance currentProcessInstance = null;
    static String workItemId_1 = null;
    static String workItemId_3 = null;
    static String workItemId_4 = null;
    
    /**
     * �������ʵ��ִ��ʵ���run������
     */
    @Test
    public void testStartNewProcess() {
        System.out.println("--------------Start a new process ----------------");
        currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();

                    //��"/workflowdefinition/example_workflow.xml"�еġ��ͻ���̡�
                    IProcessInstance processInstance = workflowSession.createProcessInstance("MultiTasksPerActivity",CurrentUserAssignmentHandlerMock.ACTOR_ID);

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
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "MultiTasksPerActivity.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1,((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List<IToken> tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        IToken token = (IToken)tokenList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "MultiTasksPerActivity", "MultiTasksPerActivity.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItemId_1 = ((IWorkItem) workItemList.get(0)).getId();


    }
    
    @Test
    public void testCompleteWorkitem1() {
    	currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();

                    //��"/workflowdefinition/example_workflow.xml"�еġ��ͻ���̡�
                    IProcessInstance processInstance = workflowSession.createProcessInstance("MultiTasksPerActivity",CurrentUserAssignmentHandlerMock.ACTOR_ID);

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
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "MultiTasksPerActivity.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(1,((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        List<IToken> tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        IToken token = (IToken)tokenList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "MultiTasksPerActivity", "MultiTasksPerActivity.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        workItemId_1 = ((IWorkItem) workItemList.get(0)).getId();
        //-------------------------------------------
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();

                    IWorkItem wi = workflowSession.findWorkItemById(workItemId_1);
                    
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
        assertNotNull(currentProcessInstance);

        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "MultiTasksPerActivity.Activity3");
        assertNotNull(taskInstanceList);
        assertEquals(3, taskInstanceList.size());
        assertEquals(new Integer(ITaskInstance.COMPLETED), ((ITaskInstance) taskInstanceList.get(0)).getState());
        assertEquals(2,((ITaskInstance) taskInstanceList.get(0)).getStepNumber().intValue());

        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(2,tokenList.size());


        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "MultiTasksPerActivity", "MultiTasksPerActivity.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        workItemId_3 = ((IWorkItem) workItemList.get(0)).getId();

        //Task4Ӧ�ò���ʵ��
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "MultiTasksPerActivity", "MultiTasksPerActivity.Activity4.Task4");
        if(workItemList==null){
        	assertNull(workItemList);
        }else{
        	assertEquals(new Integer(0),new Integer(workItemList.size()));
        }
    }    

}
