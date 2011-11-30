/**
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.persistence.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.impl.WorkItem;
import org.fireflow.engine.test.support.FireFlowAbstractTests;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.impl.Token;
import org.fireflow.model.FormTask;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.io.Dom4JFPDLParser;
import org.fireflow.model.io.FPDLParserException;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on 2009-03-08
 */
public class PersistenceServiceHibernateImplTest extends FireFlowAbstractTests {

//    private final static String springConfigFile = "config/TestContext.xml";
//    private static ClassPathResource resource = null;
//    private static XmlBeanFactory beanFactory = null;
//    private static IPersistenceService persistenceService = null;
//    private static TransactionTemplate transactionTemplate = null;
    
    SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //----------variable-------------------------------
    private static String workflowDefinitionId4Goods_Deliver_Process_ID = null;
    private static Integer theLatestVersionNumber4Goods_Deliver_Process_ID = null;
    
    private static String Goods_Deliver_Process_ID = null;
    private static String JumpTo_ID = null;
    private static String aliveProcessInstanceId = null;//用于测试findProcessInstanceById和findAliveProcessInstanceById();
    private static String aliveTaskInstanceId = null;
    private static ITaskInstance taskInstance = null;
    private static String tokenId = null;
    private static String workItemId = null;
    //--------constant----------------------------
    final String processId = "ProcessId_4_Test";
    final String activityId = "ActivityId_4_Test";
    final Integer state = 0;
    final Integer version = 1;
    final Date createdTime = new Date();
    final Date endTime = new Date();
    final Date expiredTime = new Date();
    final Date startedTime = new Date();
    final String displayName = "测试PersistenceService";
    final String name = "Test PersistenceService";
    final String assignmentStrategy = FormTask.ANY;
    final String taskId = "TaskId_4_Test";
    final String taskType = Task.FORM;
    final String actorId = "Fire Workflow Tester";
    final String comments = "This is JUnit test";
    final Date claimedTime = new Date();
    final Boolean alive = Boolean.TRUE;
    final Integer tokenValue = 1;
    final String cancelComments = "Canceled by JUnit Tester";
    
    public PersistenceServiceHibernateImplTest() {
    }

//    @BeforeClass
//    public static void setUpClass() throws Exception {
//        resource = new ClassPathResource(springConfigFile);
//        beanFactory = new XmlBeanFactory(resource);
//        persistenceService = (IPersistenceService) beanFactory.getBean("persistenceService");
//        transactionTemplate = (TransactionTemplate) beanFactory.getBean("transactionTemplate");
//        RuntimeContext rtCtx = (RuntimeContext) beanFactory.getBean("runtimeContext");
//        persistenceService.setRuntimeContext(rtCtx);
//
//        //首先将表中的数据清除
//        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//
//            @Override
//            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
//                FireWorkflowHelperDao helperDao = (FireWorkflowHelperDao) beanFactory.getBean("FireWorkflowHelperDao");
//                helperDao.clearAllTables();
//            }
//        });
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }

    /**
     * Test of setRuntimeContext method, of class PersistenceServiceHibernateImpl.
     */
    @Ignore
    @Test
    public void testSetRuntimeContext() {
        System.out.println("setRuntimeContext");
        RuntimeContext ctx = null;
        PersistenceServiceHibernateImpl instance = new PersistenceServiceHibernateImpl();
        instance.setRuntimeContext(ctx);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRuntimeContext method, of class PersistenceServiceHibernateImpl.
     */
    @Ignore
    @Test
    public void testGetRuntimeContext() {
        System.out.println("getRuntimeContext");
        PersistenceServiceHibernateImpl instance = new PersistenceServiceHibernateImpl();
        RuntimeContext expResult = null;
        RuntimeContext result = instance.getRuntimeContext();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveOrUpdateWorkflowDefinition method, of class PersistenceServiceHibernateImpl.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSaveOrUpdateWorkflowDefinition() {
        System.out.println("--------saveOrUpdateWorkflowDefinition--------");
        final List<String> workflowProcessFileNames = new ArrayList();
        workflowProcessFileNames.add("/workflowdefinition/example_workflow.xml");
        workflowProcessFileNames.add("/workflowdefinition/Jump.xml");
        List<String> workflowProcessIds = (List<String>) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                List<String> ids = new ArrayList<String>();
                if (workflowProcessFileNames != null) {
                    Dom4JFPDLParser parser = new Dom4JFPDLParser();
                    for (int i = 0; i < workflowProcessFileNames.size(); i++) {
                        try {
                            InputStream inStream = this.getClass().getResourceAsStream(workflowProcessFileNames.get(i).trim());
                            WorkflowProcess workflowProcess = parser.parse(inStream);
                            WorkflowDefinition workflowDef = new WorkflowDefinition();
                            workflowDef.setWorkflowProcess(workflowProcess);
                            ids.add(workflowProcess.getId());
                            persistenceService.saveOrUpdateWorkflowDefinition(workflowDef);
                        } catch (IOException ex) {
                            Logger.getLogger(PersistenceServiceHibernateImplTest.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FPDLParserException ex) {
                            Logger.getLogger(PersistenceServiceHibernateImplTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                return ids;
            }

        });
        assertNotNull(workflowProcessIds);
        assertEquals(2,workflowProcessIds.size());
        Goods_Deliver_Process_ID = workflowProcessIds.get(0);
        JumpTo_ID = workflowProcessIds.get(1);

        WorkflowDefinition def1 = persistenceService.findTheLatestVersionOfWorkflowDefinitionByProcessId(Goods_Deliver_Process_ID);
        assertNotNull(def1);
        workflowDefinitionId4Goods_Deliver_Process_ID = def1.getId();
        theLatestVersionNumber4Goods_Deliver_Process_ID = def1.getVersion();
        System.out.println("The latest version number of "+Goods_Deliver_Process_ID+" is "+def1.getVersion());
        System.out.println("======================The workflow process is ===========");
        System.out.println(def1.getProcessContent());
        assertNotNull(def1.getWorkflowProcess());
        assertEquals(Goods_Deliver_Process_ID,def1.getWorkflowProcess().getId());

        WorkflowDefinition def2 = persistenceService.findTheLatestVersionOfWorkflowDefinitionByProcessId(JumpTo_ID);
        assertNotNull(def2);
        System.out.println("The latest version number of "+JumpTo_ID+" is "+def2.getVersion());
        System.out.println("======================The workflow process is ===========");
        System.out.println(def2.getProcessContent());        
        assertNotNull(def2.getWorkflowProcess());
        assertEquals(JumpTo_ID,def2.getWorkflowProcess().getId());        
    }

    /**
     * Test of saveOrUpdateProcessInstance method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testSaveOrUpdateProcessInstance() {
        System.out.println("--------saveOrUpdateProcessInstance--------");
        final String parentProcessInstanceId = "parent process instance 123";
        final String parentTaskInstanceId = "parent task instance 123";

        IProcessInstance processInstance1 = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {

                ProcessInstance processInstance = new ProcessInstance();
                processInstance.setCreatedTime(createdTime);
                processInstance.setDisplayName(displayName);
                processInstance.setEndTime(endTime);
                processInstance.setExpiredTime(expiredTime);
                processInstance.setName(name);
                processInstance.setParentProcessInstanceId(parentProcessInstanceId);
                processInstance.setParentTaskInstanceId(parentTaskInstanceId);
                processInstance.setProcessId(processId);
                processInstance.setStartedTime(startedTime);
                processInstance.setState(state);
                processInstance.setVersion(version);

                persistenceService.saveOrUpdateProcessInstance(processInstance);

                return processInstance;
            }
        });

        System.out.println("The new process instance id  = " + processInstance1.getId());
        aliveProcessInstanceId = processInstance1.getId();
        IProcessInstance processInstance2 = persistenceService.findProcessInstanceById(processInstance1.getId());

        assertFalse(processInstance1.hashCode() == processInstance2.hashCode());
        assertEquals(processInstance1.getId(), processInstance2.getId());
        assertEquals(processInstance2.getName(), name);
        assertEquals(processInstance2.getDisplayName(), displayName);
        assertEquals(processInstance2.getProcessId(), processId);
        assertEquals(dFormat.format(processInstance2.getCreatedTime()), dFormat.format(createdTime));
        assertEquals(dFormat.format(processInstance2.getStartedTime()), dFormat.format(startedTime));
        assertEquals(dFormat.format(processInstance2.getExpiredTime()), dFormat.format(expiredTime));
        assertEquals(dFormat.format(processInstance2.getEndTime()), dFormat.format(endTime));
        assertEquals(processInstance2.getState(), state);
        assertEquals(processInstance2.getVersion(), version);
        assertEquals(processInstance2.getParentProcessInstanceId(), parentProcessInstanceId);
        assertEquals(processInstance2.getParentTaskInstanceId(), parentTaskInstanceId);
    }

    /**
     * Test of saveOrUpdateToken method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testSaveOrUpdateToken() {
        System.out.println("--------saveOrUpdateToken--------");

        IToken token1 = (IToken) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                Token token = new Token();
                token.setAlive(alive);
                token.setNodeId(activityId);
                token.setProcessInstanceId(aliveProcessInstanceId);
                token.setValue(tokenValue);

                persistenceService.saveOrUpdateToken(token);

                return token;
            }
        });

        System.out.println("The new token id = " + token1.getId());
        this.tokenId = token1.getId();

        IToken token2 = persistenceService.findTokenById(tokenId);
        assertFalse(token1.hashCode() == token2.hashCode());
        assertEquals(token1.getId(), token2.getId());
        assertEquals(token2.getNodeId(), activityId);
        assertEquals(token2.getProcessInstanceId(), aliveProcessInstanceId);
        assertEquals(token2.getValue(), tokenValue);
    }

    /**
     * Test of saveOrUpdateTaskInstance method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testSaveOrUpdateTaskInstance() {
        System.out.println("--------saveOrUpdateTaskInstance--------");

        ITaskInstance taskInstance1 = (ITaskInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                TaskInstance taskInstance = new TaskInstance();
                taskInstance.setActivityId(activityId);
                taskInstance.setAssignmentStrategy(assignmentStrategy);
                taskInstance.setCreatedTime(createdTime);
                taskInstance.setDisplayName(displayName);
                taskInstance.setEndTime(endTime);
                taskInstance.setExpiredTime(expiredTime);
                taskInstance.setName(name);
                taskInstance.setProcessId(processId);
                taskInstance.setProcessInstanceId(aliveProcessInstanceId);
                taskInstance.setStartedTime(startedTime);
                taskInstance.setState(state);
                taskInstance.setTargetActivityId(activityId);
                taskInstance.setTaskId(taskId);
                taskInstance.setTaskType(taskType);
                taskInstance.setVersion(version);
//                taskInstance.setTokenId(tokenId);
                persistenceService.saveOrUpdateTaskInstance(taskInstance);
                return taskInstance;
            }
        });

        System.out.println("The new task instance id  = " + taskInstance1.getId());
        aliveTaskInstanceId = taskInstance1.getId();
        ITaskInstance taskInstance2 = persistenceService.findTaskInstanceById(aliveTaskInstanceId);
        taskInstance = taskInstance2;

        assertFalse(taskInstance1.hashCode() == taskInstance2.hashCode());
        assertEquals(taskInstance1.getId(), taskInstance2.getId());
        assertEquals(taskInstance2.getName(), name);
        assertEquals(taskInstance2.getDisplayName(), displayName);
        assertEquals(taskInstance2.getProcessId(), processId);
        assertEquals(dFormat.format(taskInstance2.getCreatedTime()), dFormat.format(createdTime));
        assertEquals(dFormat.format(taskInstance2.getStartedTime()), dFormat.format(startedTime));
        assertEquals(dFormat.format(taskInstance2.getExpiredTime()), dFormat.format(expiredTime));
        assertEquals(dFormat.format(taskInstance2.getEndTime()), dFormat.format(endTime));
        assertEquals(taskInstance2.getState(), state);
        assertEquals(taskInstance2.getVersion(), version);
        assertEquals(taskInstance2.getActivityId(), activityId);
        assertEquals(taskInstance2.getAssignmentStrategy(), assignmentStrategy);
        assertEquals(taskInstance2.getProcessInstanceId(), aliveProcessInstanceId);
        assertEquals(taskInstance2.getTargetActivityId(), activityId);
        assertEquals(taskInstance2.getTaskId(), taskId);
        assertEquals(taskInstance2.getTaskType(), taskType);

    }

    /**
     * Test of saveOrUpdateWorkItem method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testSaveOrUpdateWorkItem() {
        System.out.println("--------saveOrUpdateWorkItem--------");

        IWorkItem workItem1 = (IWorkItem) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                WorkItem wi = new WorkItem();
                wi.setActorId(actorId);
                wi.setComments(comments);
                wi.setCreatedTime(createdTime);
                wi.setEndTime(endTime);
                wi.setClaimedTime(claimedTime);
                wi.setState(state);
                wi.setTaskInstance(taskInstance);

                persistenceService.saveOrUpdateWorkItem(wi);
                return wi;
            }
        });

        System.out.print("The new work item id = " + workItem1.getId());
        workItemId = workItem1.getId();
        IWorkItem workItem2 = persistenceService.findWorkItemById(workItem1.getId());

        assertFalse(workItem1.hashCode() == workItem2.hashCode());
        assertEquals(workItem1.getId(), workItem2.getId());
        assertEquals(workItem1.getTaskInstance().getId(), this.aliveTaskInstanceId);
        assertEquals(workItem2.getTaskInstance().getName(), name);
        assertEquals(workItem2.getTaskInstance().getDisplayName(), displayName);
        assertEquals(workItem2.getTaskInstance().getProcessId(), processId);
        assertEquals(dFormat.format(workItem2.getCreatedTime()), dFormat.format(createdTime));
        assertEquals(dFormat.format(workItem2.getClaimedTime()), dFormat.format(claimedTime));
        assertEquals(dFormat.format(workItem2.getEndTime()), dFormat.format(endTime));
        assertEquals(workItem2.getState(), state);
        assertEquals(workItem2.getActorId(), actorId);
        assertEquals(workItem2.getComments(), comments);
    }

    /**
     * Test of getAliveTokenCountForNode method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testGetAliveTokenCountForNode() {
        System.out.println("--------getAliveTokenCountForNode--------");
        Integer aliveTokenCount = persistenceService.getAliveTokenCountForNode(aliveProcessInstanceId, activityId);
        System.out.println("Alive token count is " + aliveTokenCount);
        assertEquals(aliveTokenCount, new Integer(1));
    }

    /**
     * Test of getAliveTaskInstanceCountForActivity method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testGetAliveTaskInstanceCountForActivity() {
        System.out.println("--------getAliveTaskInstanceCountForActivity--------");
        Integer aliveTaskInstanceCount = persistenceService.getAliveTaskInstanceCountForActivity(aliveProcessInstanceId, activityId);
        System.out.println("Alive task instance count is " + aliveTaskInstanceCount);
        assertEquals(aliveTaskInstanceCount, new Integer(1));
    }

    /**
     * Test of findInitializedTaskInstancesListForToken method, of class PersistenceServiceHibernateImpl.
     */
//    @Test
//    public void testFindInitializedTaskInstancesListForToken() {
//        System.out.println("--------findInitializedTaskInstancesListForToken--------");
//        List list = persistenceService.findInitializedTaskInstancesListForToken(aliveProcessInstanceId, tokenId);
//        assertNotNull(list);
//        assertEquals(1, list.size());
//    }

    /**
     * Test of findTaskInstancesForProcessInstance method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindTaskInstancesForProcessInstance() {
        System.out.println("--------findTaskInstancesForProcessInstance--------");
        List list = persistenceService.findTaskInstancesForProcessInstance(aliveProcessInstanceId, activityId);
        assertNotNull(list);
        assertEquals(list.size(), 1);
    }

    /**
     * Test of findTokenById method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindTokenById() {
        System.out.println("--------findTokenById--------");
        IToken token = persistenceService.findTokenById(tokenId);
        assertNotNull(token);
        assertEquals(token.getProcessInstanceId(), aliveProcessInstanceId);
    }

    /**
     * Test of findTokensForProcessInstance method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindTokensForProcessInstance() {
        System.out.println("--------findTokensForProcessInstance--------");
        List list = persistenceService.findTokensForProcessInstance(aliveProcessInstanceId, activityId);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    /**
     * Test of findWorkItemById method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindWorkItemById() {
        System.out.println("--------findWorkItemById--------");
        IWorkItem wi = persistenceService.findWorkItemById(workItemId);
        assertNotNull(wi);
    }

    /**
     * Test of findAliveTaskInstanceById method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindAliveTaskInstanceById() {
        System.out.println("--------findAliveTaskInstanceById--------");
        ITaskInstance taskInstance = persistenceService.findAliveTaskInstanceById(aliveTaskInstanceId);
        assertNotNull(taskInstance);
    }

    /**
     * Test of findTaskInstanceById method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindTaskInstanceById() {
        System.out.println("--------findTaskInstanceById--------");
        ITaskInstance taskInstance = persistenceService.findTaskInstanceById(aliveTaskInstanceId);
        assertNotNull(taskInstance);
    }

    /**
     * Test of findAliveWorkItemsWithoutJoinForTaskInstance method, of class PersistenceServiceHibernateImpl.
     */
//    @Test
//    public void testFindAliveWorkItemsWithoutJoinForTaskInstance() {
//        System.out.println("--------findAliveWorkItemsWithoutJoinForTaskInstance--------");
//        List list = persistenceService.findAliveWorkItemsWithoutJoinForTaskInstance(aliveTaskInstanceId);
//        assertNotNull(list);
//        assertEquals(1,list.size());
//        IWorkItem wi = (IWorkItem)list.get(0);
//    }
    /**
     * Test of findAliveWorkItemCountForTaskInstance method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindAliveWorkItemCountForTaskInstance() {
        System.out.println("--------findAliveWorkItemCountForTaskInstance--------");
        Integer aliveWorkItemCount = persistenceService.getAliveWorkItemCountForTaskInstance(aliveTaskInstanceId);
        assertEquals(new Integer(1), aliveWorkItemCount);
    }

    /**
     * Test of findDeadWorkItemsWithoutJoinForTaskInstance method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindDeadWorkItemsWithoutJoinForTaskInstance() {
        System.out.println("--------findDeadWorkItemsWithoutJoinForTaskInstance--------");
        IWorkItem workItem1 = (IWorkItem) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                WorkItem wi = new WorkItem();
                wi.setActorId(actorId);
                wi.setComments(comments);
                wi.setCreatedTime(createdTime);
                wi.setEndTime(endTime);
                wi.setClaimedTime(claimedTime);
                wi.setState(IWorkItem.COMPLETED);
                wi.setTaskInstance(taskInstance);

                persistenceService.saveOrUpdateWorkItem(wi);
                return wi;
            }
        });

        List list = persistenceService.findCompletedWorkItemsForTaskInstance(aliveTaskInstanceId);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    /**
     * Test of findWorkItemForTask method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindWorkItemForTask() {
        System.out.println("--------findWorkItemForTask--------");
        List list = persistenceService.findWorkItemsForTask(taskId);
        assertNotNull(list);
        assertEquals(2, list.size());
    }

    /**
     * Test of findProcessInstanceByProcessId method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindProcessInstanceByProcessId() {
        System.out.println("--------findProcessInstanceByProcessId--------");
        List processInstanceList = persistenceService.findProcessInstancesByProcessId(this.processId);
        assertNotNull(processInstanceList);
        System.out.println("The processInstanceList size is " + processInstanceList.size());
    }

    /**
     * Test of findProcessInstanceByProcessIdAndVersion method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindProcessInstanceByProcessIdAndVersion() {
        System.out.println("--------findProcessInstanceByProcessIdAndVersion---------");
        List processInstanceList = persistenceService.findProcessInstancesByProcessIdAndVersion(this.processId, new Integer(1));
        assertNotNull(processInstanceList);
        System.out.println("The processInstanceList size is " + processInstanceList.size());
    }

    /**
     * Test of findProcessInstanceById method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindProcessInstanceById() {
        System.out.println("--------findProcessInstanceById--------");
        IProcessInstance processInstance = persistenceService.findProcessInstanceById(this.aliveProcessInstanceId);
        assertNotNull(processInstance);
        assertEquals(processInstance.getProcessId(), this.processId);
    }

    /**
     * Test of findAliveProcessInstanceById method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindAliveProcessInstanceById() {
        System.out.println("--------findAliveProcessInstanceById--------");
        IProcessInstance processInstance = persistenceService.findAliveProcessInstanceById(this.aliveProcessInstanceId);
        assertNotNull(processInstance);
        assertEquals(processInstance.getProcessId(), this.processId);
    }

    /**
     * Test of findTheLatestVersionOfWorkflowDefinition method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindTheLatestVersionNumber() {
        System.out.println("--------FindTheLatestVersionNumber--------");
        Integer version = persistenceService.findTheLatestVersionNumber(this.Goods_Deliver_Process_ID);
        assertNotNull(version);
        assertEquals(this.theLatestVersionNumber4Goods_Deliver_Process_ID,version);
    }

    /**
     * Test of findWorkflowDefinitionById method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindWorkflowDefinitionById() {
        System.out.println("--------findWorkflowDefinitionById--------");
        WorkflowDefinition workflowDef = persistenceService.findWorkflowDefinitionById(this.workflowDefinitionId4Goods_Deliver_Process_ID);
        assertNotNull(workflowDef);
        assertEquals(this.theLatestVersionNumber4Goods_Deliver_Process_ID,workflowDef.getVersion());
        assertEquals(this.Goods_Deliver_Process_ID,workflowDef.getWorkflowProcess().getId());
    }

    /**
     * Test of findWorkflowDefinitionByProcessIdAndVersion method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindWorkflowDefinitionByProcessIdAndVersionNumber() {
        System.out.println("--------findWorkflowDefinitionByProcessIdAndVersion--------");
        WorkflowDefinition workflowDef = persistenceService.findWorkflowDefinitionByProcessIdAndVersionNumber(this.Goods_Deliver_Process_ID,theLatestVersionNumber4Goods_Deliver_Process_ID);
        assertNotNull(workflowDef);
        assertEquals(this.theLatestVersionNumber4Goods_Deliver_Process_ID,workflowDef.getVersion());
        assertEquals(this.Goods_Deliver_Process_ID,workflowDef.getWorkflowProcess().getId());
    }

    /**
     * Test of findLatestVersionOfWorkflowDefinitionByProcessId method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindLatestVersionOfWorkflowDefinitionByProcessId() {
        System.out.println("--------findLatestVersionOfWorkflowDefinitionByProcessId--------");
        WorkflowDefinition workflowDef = persistenceService.findTheLatestVersionOfWorkflowDefinitionByProcessId(this.Goods_Deliver_Process_ID);
        assertNotNull(workflowDef);
        assertEquals(this.theLatestVersionNumber4Goods_Deliver_Process_ID,workflowDef.getVersion());
        assertEquals(this.Goods_Deliver_Process_ID,workflowDef.getWorkflowProcess().getId());
    }

    /**
     * Test of findWorkflowDefinitionByProcessId method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindWorkflowDefinitionsByProcessId() {
        System.out.println("--------findWorkflowDefinitionByProcessId--------");
        List workflowDefList = persistenceService.findWorkflowDefinitionsByProcessId(this.Goods_Deliver_Process_ID);
        assertNotNull(workflowDefList);
        System.out.println("There are "+workflowDefList.size()+" workflow definitions");
        System.out.println(((WorkflowDefinition)workflowDefList.get(0)).getProcessContent());
    }

    /**
     * Test of findAllLatestVersionOfWorkflowDefinition method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindAllLatestVersionsOfWorkflowDefinition() {
        System.out.println("--------findAllLatestVersionOfWorkflowDefinition--------");
        List workflowDefList = persistenceService.findAllTheLatestVersionsOfWorkflowDefinition();
        assertNotNull(workflowDefList);
        assertEquals(2,workflowDefList.size());
        for (int i=0;i<workflowDefList.size();i++){
            WorkflowDefinition workflowDef = (WorkflowDefinition)workflowDefList.get(i);
            if (this.Goods_Deliver_Process_ID.equals(workflowDef.getProcessId())){
                assertEquals(this.theLatestVersionNumber4Goods_Deliver_Process_ID,workflowDef.getVersion());
            }
            System.out.println("Find workflow definition id="+workflowDef.getProcessId()+" and version="+workflowDef.getVersion());

        }
    }

    /**
     * Test of findTodoWorkItems method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindTodoWorkItems_String() {
        System.out.println("--------findTodoWorkItems--------");
        List todoWorkItemsList = persistenceService.findTodoWorkItems(actorId);
        assertNotNull(todoWorkItemsList);
        assertEquals(todoWorkItemsList.size(), 1);
    }

    /**
     * Test of findTodoWorkItems method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindTodoWorkItems_String_String() {
        System.out.println("--------findTodoWorkItems--------");
        List todoWorkItemsList = persistenceService.findTodoWorkItems(actorId, aliveProcessInstanceId);
        assertNotNull(todoWorkItemsList);
        assertEquals(todoWorkItemsList.size(), 1);
    }

    /**
     * Test of findTodoWorkItems method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindTodoWorkItems_3args() {
        System.out.println("--------findTodoWorkItems--------");
        List todoWorkItemsList = persistenceService.findTodoWorkItems(actorId, processId, taskId);
        assertNotNull(todoWorkItemsList);
        assertEquals(1, todoWorkItemsList.size());
    }

    /**
     * Test of findHaveDoneWorkItems method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindHaveDoneWorkItems_String() {
        System.out.println("--------findHaveDoneWorkItems--------");
        List todoWorkItemsList = persistenceService.findHaveDoneWorkItems(actorId);
        assertNotNull(todoWorkItemsList);
        assertEquals(todoWorkItemsList.size(), 1);
    }

    /**
     * Test of findHaveDoneWorkItems method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindHaveDoneWorkItems_String_String() {
        System.out.println("--------findHaveDoneWorkItems--------");
        List todoWorkItemsList = persistenceService.findHaveDoneWorkItems(actorId, aliveProcessInstanceId);
        assertNotNull(todoWorkItemsList);
        assertEquals(todoWorkItemsList.size(), 1);
    }

    /**
     * Test of findHaveDoneWorkItems method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testFindHaveDoneWorkItems_3args() {
        System.out.println("--------findHaveDoneWorkItems--------");
        List todoWorkItemsList = persistenceService.findHaveDoneWorkItems(actorId, processId, taskId);
        assertNotNull(todoWorkItemsList);
        assertEquals(todoWorkItemsList.size(), 1);
    }

    /**
     * Test of deleteWorkItemsInInitializedState method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testDeleteWorkItemsInInitializedState() {
        System.out.println("--------deleteWorkItemsInInitializedState--------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                persistenceService.deleteWorkItemsInInitializedState(aliveTaskInstanceId);
            }
        });

        List list = persistenceService.findWorkItemsForTask(taskId);
        if (list != null) {
            assertEquals(1, list.size());
        } else {
            assertNull(list);
        }
    }

    /**
     * Test of deleteTokensForNode method, of class PersistenceServiceHibernateImpl.
     */
    @Test
    public void testDeleteTokensForNode() {
        System.out.println("--------deleteTokensForNode--------");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                persistenceService.deleteTokensForNode(aliveProcessInstanceId, activityId);
            }
        });

        List list = persistenceService.findTokensForProcessInstance(aliveProcessInstanceId, activityId);
        if (list != null) {
            assertEquals(0, list.size());
        } else {
            assertNull(list);
        }
    }

    @Test
    public void testCancelAliveWorkItemsForTaskInstance() {
        System.out.println("--------cancelAliveWorkItemsForTaskInstance--------");

        IWorkItem workItem1 = (IWorkItem) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                WorkItem wi = new WorkItem();
                wi.setActorId(actorId);
                wi.setComments(comments);
                wi.setCreatedTime(createdTime);
                wi.setEndTime(endTime);
                wi.setClaimedTime(claimedTime);
                wi.setState(state);
                wi.setTaskInstance(taskInstance);

                persistenceService.saveOrUpdateWorkItem(wi);
                return wi;
            }
        });
        final String workItemIdTmp = workItem1.getId();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
//                persistenceService.abortTaskInstance(aliveTaskInstanceId, cancelComments);
            }
        });

        IWorkItem workItem2 = persistenceService.findWorkItemById(workItemIdTmp);
        assertEquals(new Integer(IWorkItem.CANCELED), workItem2.getState());
        assertEquals(cancelComments, workItem2.getComments());
    }
}