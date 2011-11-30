package org.fireflow.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.AssignmentHandlerMock;
import org.fireflow.engine.taskinstance.CurrentUserAssignmentHandlerMock;
import org.fireflow.engine.test.support.FireFlowAbstractTests;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.KernelException;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

public class AComplexJump extends FireFlowAbstractTests{

    //--------constant----------------------
    //客户电话，用于控制是否执行“发送手机短信通知客户收货”。通过设置mobile等于null和非null值分别进行测试。
    //private final static String mobile = "";//null;//"123123123123";

    //-----variables-----------------
//
//    static IProcessInstance currentProcessInstance = null;
//    static String workItemId_1 = null;
//    static String workItemId_2 = null;
//    static String workItemId_8 = null;    
//    static String workItemId_12 = null;
//    static String workItemId_13 = null;
//    static String workItemId_14 = null;
       
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
                    System.out.println("====================workflowSession in start process is "+workflowSession.hashCode());

                    IProcessInstance processInstance = workflowSession.createProcessInstance("AComplexJump",AssignmentHandlerMock.ACTOR_ID);

                    processInstance.run();//启动流程实例并开始运行
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

        List<IToken> tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        IToken token =  tokenList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), workItemList.get(0).getState());

        final String workItemId_1 = workItemList.get(0).getId();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(workItemId_1);
                    paymentTaskWorkItem.complete("this is a comments");  //结束第一个activity节点
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        
        //找到activity2的工单
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        //String workItemId_2 = ((IWorkItem) workItemList.get(0)).getId();
    }    
    
    @Test
    public void testJumpFromAct2ToAct12() {
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    System.out.println("====================workflowSession in start process is "+workflowSession.hashCode());

                    IProcessInstance processInstance = workflowSession.createProcessInstance("AComplexJump",AssignmentHandlerMock.ACTOR_ID);

                    processInstance.run();//启动流程实例并开始运行
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

        List<IToken> tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        IToken token =  tokenList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), workItemList.get(0).getState());

        final String workItemId_1 = workItemList.get(0).getId();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(workItemId_1);
                    paymentTaskWorkItem.complete("this is a comments");  //结束第一个activity节点
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        
        //找到activity2的工单
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_2 = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_2);
                    workItem2.claim();//签收
                    //直接由activity2节点，自由流到activity12
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_2, "AComplexJump.Activity12", null, "testJumpFromAct2ToAct12");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        //Synchronizer4 的token数量为1，而且容量为1 并且是dead状态
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity12.Task12");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        //String workItemId_12 = ((IWorkItem) workItemList.get(0)).getId();        
    }
    
    @Test
    public void testJumpFromAct12ToAct13() {
    	
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    System.out.println("====================workflowSession in start process is "+workflowSession.hashCode());

                    IProcessInstance processInstance = workflowSession.createProcessInstance("AComplexJump",AssignmentHandlerMock.ACTOR_ID);

                    processInstance.run();//启动流程实例并开始运行
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

        List<IToken> tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        IToken token =  tokenList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), workItemList.get(0).getState());

        final String workItemId_1 = workItemList.get(0).getId();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(workItemId_1);
                    paymentTaskWorkItem.complete("this is a comments");  //结束第一个activity节点
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        
        //找到activity2的工单
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_2 = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_2);
                    workItem2.claim();//签收
                    //直接由activity2节点，自由流到activity12
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_2, "AComplexJump.Activity12", null, "testJumpFromAct2ToAct12");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        //Synchronizer4 的token数量为1，而且容量为1 并且是dead状态
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity12.Task12");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_12 = ((IWorkItem) workItemList.get(0)).getId();  
    	//------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_12);
                    workItem2.claim();
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_12, "AComplexJump.Activity13", null, "testJumpFromAct12ToAct13");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity13.Task13");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        //String workItemId_13 = ((IWorkItem) workItemList.get(0)).getId();         	
    }    
    @Test
    public void testJumpFromAct13ToAct14() {
    	
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    System.out.println("====================workflowSession in start process is "+workflowSession.hashCode());

                    IProcessInstance processInstance = workflowSession.createProcessInstance("AComplexJump",AssignmentHandlerMock.ACTOR_ID);

                    processInstance.run();//启动流程实例并开始运行
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

        List<IToken> tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        IToken token =  tokenList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), workItemList.get(0).getState());

        final String workItemId_1 = workItemList.get(0).getId();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(workItemId_1);
                    paymentTaskWorkItem.complete("this is a comments");  //结束第一个activity节点
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        
        //找到activity2的工单
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_2 = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_2);
                    workItem2.claim();//签收
                    //直接由activity2节点，自由流到activity12
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_2, "AComplexJump.Activity12", null, "testJumpFromAct2ToAct12");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        //Synchronizer4 的token数量为1，而且容量为1 并且是dead状态
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity12.Task12");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_12 = ((IWorkItem) workItemList.get(0)).getId();  
    	//------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_12);
                    workItem2.claim();
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_12, "AComplexJump.Activity13", null, "testJumpFromAct12ToAct13");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity13.Task13");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_13 = ((IWorkItem) workItemList.get(0)).getId();  
    	//--------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_13);
                    workItem2.claim();
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_13, "AComplexJump.Activity14", null, "testJumpFromAct13ToAct14");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity14.Task14");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        //String workItemId_14 = ((IWorkItem) workItemList.get(0)).getId();       	
    }    
    
    @Test
    public void testJumpFromAct14ToAct12() {
    	
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    System.out.println("====================workflowSession in start process is "+workflowSession.hashCode());

                    IProcessInstance processInstance = workflowSession.createProcessInstance("AComplexJump",AssignmentHandlerMock.ACTOR_ID);

                    processInstance.run();//启动流程实例并开始运行
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

        List<IToken> tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        IToken token =  tokenList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), workItemList.get(0).getState());

        final String workItemId_1 = workItemList.get(0).getId();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(workItemId_1);
                    paymentTaskWorkItem.complete("this is a comments");  //结束第一个activity节点
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        
        //找到activity2的工单
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_2 = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_2);
                    workItem2.claim();//签收
                    //直接由activity2节点，自由流到activity12
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_2, "AComplexJump.Activity12", null, "testJumpFromAct2ToAct12");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        //Synchronizer4 的token数量为1，而且容量为1 并且是dead状态
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity12.Task12");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_12 = ((IWorkItem) workItemList.get(0)).getId();  
    	//------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_12);
                    workItem2.claim();
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_12, "AComplexJump.Activity13", null, "testJumpFromAct12ToAct13");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity13.Task13");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_13 = ((IWorkItem) workItemList.get(0)).getId();  
    	//--------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_13);
                    workItem2.claim();
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_13, "AComplexJump.Activity14", null, "testJumpFromAct13ToAct14");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity14.Task14");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_14 = ((IWorkItem) workItemList.get(0)).getId();   
    	//-----------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_14);
                    workItem2.claim();
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_14, "AComplexJump.Activity12", null, "testJumpFromAct14ToAct12");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity12.Task12");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());
        
        //String workItemId_121 = ((IWorkItem) workItemList.get(0)).getId();        	
    }    
    
    @Test
    public void testJumpFromAct12ToAct8() {
    	
    	IProcessInstance currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    System.out.println("====================workflowSession in start process is "+workflowSession.hashCode());

                    IProcessInstance processInstance = workflowSession.createProcessInstance("AComplexJump",AssignmentHandlerMock.ACTOR_ID);

                    processInstance.run();//启动流程实例并开始运行
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

        List<IToken> tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        IToken token =  tokenList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), workItemList.get(0).getState());

        final String workItemId_1 = workItemList.get(0).getId();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem paymentTaskWorkItem = workflowSession.findWorkItemById(workItemId_1);
                    paymentTaskWorkItem.complete("this is a comments");  //结束第一个activity节点
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(3,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        
        //找到activity2的工单
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity2.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_2 = ((IWorkItem) workItemList.get(0)).getId();
    	//------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_2);
                    workItem2.claim();//签收
                    //直接由activity2节点，自由流到activity12
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_2, "AComplexJump.Activity12", null, "testJumpFromAct2ToAct12");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        //Synchronizer4 的token数量为1，而且容量为1 并且是dead状态
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity12.Task12");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_12 = ((IWorkItem) workItemList.get(0)).getId();  
    	//------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_12);
                    workItem2.claim();
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_12, "AComplexJump.Activity13", null, "testJumpFromAct12ToAct13");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity13.Task13");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_13 = ((IWorkItem) workItemList.get(0)).getId();  
    	//--------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_13);
                    workItem2.claim();
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_13, "AComplexJump.Activity14", null, "testJumpFromAct13ToAct14");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity14.Task14");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_14 = ((IWorkItem) workItemList.get(0)).getId();   
    	//-----------------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_14);
                    workItem2.claim();
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_14, "AComplexJump.Activity12", null, "testJumpFromAct14ToAct12");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(4,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity12.Task12");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.RUNNING), ((IWorkItem) workItemList.get(0)).getState());
        
        final String workItemId_121 = ((IWorkItem) workItemList.get(0)).getId();
    	//-----------------------------------------------
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //IWorkItem workItem2 = workflowSession.findWorkItemById(workItemId_121);
//                    workItem2.claim();
                    workflowSession.completeWorkItemAndJumpToEx(workItemId_121, "AComplexJump.Activity8", null, "testJumpFromAct14ToAct8");
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokenList);
        assertEquals(5,tokenList.size());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer4");
        assertNotNull(tokenList);
        assertEquals(0,tokenList.size());
//        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer9");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());
        
        tokenList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), "AComplexJump.Synchronizer7");
        assertNotNull(tokenList);
        assertEquals(1,tokenList.size());
        assertEquals(1,((IToken)tokenList.get(0)).getValue().intValue());        
        
        workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "AComplexJump", "AComplexJump.Activity8.Task8");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(ITaskInstance.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        
        //String workItemId_8 = ((IWorkItem) workItemList.get(0)).getId();      	
    }       

}
