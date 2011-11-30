package org.fireflow.example.leaveapplication;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.example.leaveapplication.misc.FireWorkflowHelperDao;
import org.fireflow.example.leaveapplication.workflowextension.CurrentUserAssignmentHandler;
import org.fireflow.example.leaveapplication.workflowextension.RoleDepartmentBasedAssignmentHandler;
import org.fireflow.kernel.KernelException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 一个类展示Fire Workflow的主要API
 * 
 * @author 非也
 * 
 */
public class LeaveApplicationTester {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// 初始化测试环境，
			setUpClass();
			
			//启动流程实例
			testStartNewProcess();
			
			int reply = JOptionPane.showConfirmDialog(null, "流程实例启动成功，请从后台数据库观察WorkItem,TaskInstance,ProcessInstance,流程变量等信息。\n" +
					"下一步：申请人签收工单，填入请假天数，结束工单，是否继续？","提示信息" ,JOptionPane.YES_NO_OPTION);
			
			if (reply==JOptionPane.NO_OPTION){
				return;
			}
			
			//申请人签收工单，填入请假天数，结束工单。
			//结束工单的操作（即WorkItem.complete())会触发流程实例向下一个环节流转
			testClaimAndCompleteSubmitApplication();
			
			reply = JOptionPane.showConfirmDialog(null, "申请人操作完毕，请从后台数据库观察WorkItem,TaskInstance,ProcessInstance,流程变量等信息。\n" +
					"下一步：部门经理签收工单，填入审批意见，结束工单，是否继续？","提示信息" ,JOptionPane.YES_NO_OPTION);
			
			if (reply==JOptionPane.NO_OPTION){
				return;
			}			
			
			//部门经理签收工单，填入审批意见，结束工单
			//结束工单的操作会触发流程向下一个环节流转。在本案例中，下一个环节的含有一个ToolTask，
			//它会被流程引擎自动调用、执行。
			testClaimAndCompleteApproveApplication();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建流程实例，并执行实例的run方法启动之。 实例启动后自动实例化第一个环节和他的Task，并分配相关的WorkItem
	 */
	public static void testStartNewProcess() {
		System.out.println("\n\n=========启动流程实例开始.......");
        currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                	//IWorkflowSession是流程操作的入口，需要从runtimeContext获得。
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    
                    //创建流程实例
                    IProcessInstance processInstance = workflowSession.createProcessInstance(
                    		    "LeaveApplicationProcess",CurrentUserAssignmentHandler.APPLICANT);
                    
                    //运行流程实例
                    processInstance.run();

                    return processInstance;
                } catch (EngineException ex) {
                    Logger.getLogger(LeaveApplicationTester.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(LeaveApplicationTester.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });		
        System.out.println();
        System.out.println("===========启动流程实例结束============");
	}

	public static void testClaimAndCompleteSubmitApplication() {
		System.out.println("\n\n=========申请人签收工单、填入请假天数等信息、结束工单开始.....");
		
		transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                	//IWorkflowSession是流程操作的入口，需要从runtimeContext获得。
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    
                    List myWorkItemsList = workflowSession.findMyTodoWorkItems(CurrentUserAssignmentHandler.APPLICANT);
                    System.out.println("我(zhang)的待办workitem列表如下：");
                    for (int i=0;myWorkItemsList!=null && i<myWorkItemsList.size();i++){
                    	IWorkItem wi = (IWorkItem)myWorkItemsList.get(i);
                    	System.out.println("序号:"+i+"; id="+wi.getId()+"; name="+wi.getTaskInstance().getDisplayName()+"; actorId="+wi.getActorId()+"; state="+wi.getState());
                    }
                    
                    //选择第一个workitem进行操作
                    if (myWorkItemsList!=null && myWorkItemsList.size()>0){
                    	IWorkItem wi = (IWorkItem)myWorkItemsList.get(0);
                    	//1、首先签收
                    	wi.claim();
                    	
                    	//2、然后设置流程变量
                    	//在实际业务中，申请人应该需要填写一个表单，在这个简单的测试中就省略了……
                    	IProcessInstance processInstance = 
                    		((TaskInstance)wi.getTaskInstance()).getAliveProcessInstance();
                    	processInstance.setProcessInstanceVariable("applicant", 
                    			CurrentUserAssignmentHandler.APPLICANT);//申请人
                    	processInstance.setProcessInstanceVariable("leaveDays", 
                    			LEAVE_DAYS);//请假天数
                    	
                    	//3、结束WorkItem
                    	//结束workItem会触发流程实例向下一个环节流转，
                    	wi.complete();
                    }
                   
                    return null;
                } catch (EngineException ex) {
                    Logger.getLogger(LeaveApplicationTester.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(LeaveApplicationTester.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });		
        System.out.println("==========申请人操作结束============");
	}

	public static void testClaimAndCompleteApproveApplication() {
		System.out.println("\n\n=========部门经理签收工单、填入审批意见等信息、结束工单开始.....");
		
		transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                	//IWorkflowSession是流程操作的入口，需要从runtimeContext获得。
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    
                    List myWorkItemsList = workflowSession.findMyTodoWorkItems(RoleDepartmentBasedAssignmentHandler.DEPARTMENT_MANAGER);
                    System.out.println("我(manager_chen)的待办workitem列表如下：");
                    for (int i=0;myWorkItemsList!=null && i<myWorkItemsList.size();i++){
                    	IWorkItem wi = (IWorkItem)myWorkItemsList.get(i);
                    	System.out.println("序号:"+i+"; id="+wi.getId()+"; name="+wi.getTaskInstance().getDisplayName()+"; actorId="+wi.getActorId()+"; state="+wi.getState());
                    }
                    
                    //选择第一个workitem进行操作
                    if (myWorkItemsList!=null && myWorkItemsList.size()>0){
                    	IWorkItem wi = (IWorkItem)myWorkItemsList.get(0);
                    	//1、首先签收
                    	wi.claim();
                    	
                    	//2、然后设置流程变量
                    	//在实际业务中，审批人应该需要填写一个表单，在这个简单的测试中就省略了……
                    	IProcessInstance processInstance = 
                    		((TaskInstance)wi.getTaskInstance()).getAliveProcessInstance();
                    	processInstance.setProcessInstanceVariable("approvalFlag", 
                    			APPROVAL_FLAG);//审批意见
                    	
                    	//3、结束WorkItem
                    	//结束workItem会触发流程实例向下一个环节流转，
                    	//由于下一个环节是ToolTask，流程引擎会自动调用其ApplicationHandler.
                    	wi.complete();
                    }
                   
                    return null;
                } catch (EngineException ex) {
                    Logger.getLogger(LeaveApplicationTester.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(LeaveApplicationTester.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        });		
        System.out.println("============部门经理操作结束=========");		
	}

	public static void setUpClass() throws Exception {
		System.out.println("=========初始化环境开始.....");		
		resource = new ClassPathResource(springConfigFile);
		beanFactory = new XmlBeanFactory(resource);
		transactionTemplate = (TransactionTemplate) beanFactory
				.getBean("transactionTemplate");
		runtimeContext = (RuntimeContext) beanFactory.getBean("runtimeContext");

		// 首先将表中的数据清除
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				FireWorkflowHelperDao helperDao = (FireWorkflowHelperDao) beanFactory
						.getBean("FireWorkflowHelperDao");
				helperDao.clearAllTables();
			}
		});
		System.out.println("============初始化环境结束=================");
	}
	
	private final static String springConfigFile = "spring_config/applicationContext.xml";
	private static ClassPathResource resource = null;
	private static XmlBeanFactory beanFactory = null;// spring bean factory
	private static TransactionTemplate transactionTemplate = null;
	private static RuntimeContext runtimeContext = null;

    static IProcessInstance currentProcessInstance = null;
    
    private static final Integer LEAVE_DAYS = 3;//请假天数，
    private static final Boolean APPROVAL_FLAG = true;//审批意见，true表示同意，false表示不同意。
		
}
