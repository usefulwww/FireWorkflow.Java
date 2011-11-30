package org.fireflow.engine.persistence.springjdbc;

import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.fireflow.engine.test.support.FireFlowAbstractTests;
import org.fireflow.model.FormTask;
import org.fireflow.model.Task;
import org.junit.Test;

public class PersistenceServiceSpringJdbcImplTest extends FireFlowAbstractTests {

//    private final static String springConfigFile = "config/TestContext.xml";
//    private static ClassPathResource resource = null;
//    private static XmlBeanFactory beanFactory = null;
//    private static IPersistenceService persistenceService = null;
//    private static TransactionTemplate transactionTemplate = null;
	
    SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //----------variable-------------------------------
//    private static String workflowDefinitionId4Goods_Deliver_Process_ID = null;
//    private static Integer theLatestVersionNumber4Goods_Deliver_Process_ID = null;
//    
//    private static String Goods_Deliver_Process_ID = null;
//    private static String JumpTo_ID = null;
//    private static String aliveProcessInstanceId = null;//用于测试findProcessInstanceById和findAliveProcessInstanceById();
//    private static String aliveTaskInstanceId = null;
//    private static ITaskInstance taskInstance = null;
//    private static String tokenId = null;
//    private static String workItemId = null;
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
    
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    	try{
//	        resource = new ClassPathResource(springConfigFile);
//	        beanFactory = new XmlBeanFactory(resource);
//	        persistenceService = (IPersistenceService) beanFactory.getBean("jdbcPersistenceService");
//	        transactionTemplate = (TransactionTemplate) beanFactory.getBean("transactionTemplate");
//	        RuntimeContext rtCtx = (RuntimeContext) beanFactory.getBean("runtimeContext");
//	        persistenceService.setRuntimeContext(rtCtx);
//	
//	        //首先将表中的数据清除
//	        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//	
//	            @Override
//	            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
//	            	IFireWorkflowHelperDao helperDao = (IFireWorkflowHelperDao) beanFactory.getBean("FireWorkflowHelperDao");
//	                helperDao.clearAllTables();
//	            }
//	        });
//    	}catch(Exception ex){
//    		ex.printStackTrace();
//    	}
//        System.out.println("----setUpClass complete........");
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
    
	@Test
	public void testSaveOrUpdateProcessInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOrUpdateTaskInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOrUpdateWorkItem() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOrUpdateToken() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAliveTokenCountForNode() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCompletedTaskInstanceCountForTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAliveTaskInstanceCountForActivity() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTaskInstancesForProcessInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTaskInstancesForProcessInstanceByStepNumber() {
		fail("Not yet implemented");
	}

	@Test
	public void testLockTaskInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTokenById() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteTokensForNodes() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteTokensForNode() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteToken() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTokensForProcessInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindWorkItemById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAliveTaskInstanceById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTaskInstanceById() {
		fail("Not yet implemented");
	}

	@Test
	public void testAbortTaskInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAliveWorkItemCountForTaskInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindCompletedWorkItemsForTaskInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindWorkItemsForTaskInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindWorkItemsForTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindProcessInstancesByProcessId() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindProcessInstancesByProcessIdAndVersion() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindProcessInstanceById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAliveProcessInstanceById() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOrUpdateWorkflowDefinition() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTheLatestVersionNumber() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTheLatestVersionNumberIgnoreState() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindWorkflowDefinitionById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindWorkflowDefinitionByProcessIdAndVersionNumber() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTheLatestVersionOfWorkflowDefinitionByProcessId() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindWorkflowDefinitionsByProcessId() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAllTheLatestVersionsOfWorkflowDefinition() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTodoWorkItemsString() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTodoWorkItemsStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTodoWorkItemsStringStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindHaveDoneWorkItemsString() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindHaveDoneWorkItemsStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindHaveDoneWorkItemsStringStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteWorkItemsInInitializedState() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAliveProcessInstanceCountForParentTaskInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testSuspendProcessInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testRestoreProcessInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testAbortProcessInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOrUpdateProcessInstanceTrace() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindProcessInstanceTraces() {
		fail("Not yet implemented");
	}

}
