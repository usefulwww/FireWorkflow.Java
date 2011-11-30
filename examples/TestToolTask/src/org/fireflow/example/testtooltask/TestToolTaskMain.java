package org.fireflow.example.testtooltask;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.example.util.HsqldbManager;
import org.fireflow.kernel.KernelException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class TestToolTaskMain {
	private final static String springConfigFile = "/spring_config/applicationContext.xml";
	private static ClassPathResource resource = null;
	private static XmlBeanFactory beanFactory = null;
	private static TransactionTemplate transactionTemplate = null;
	private static RuntimeContext runtimeContext = null;

	/**
	 * 初始化运行环境
	 * 
	 * @throws Exception
	 */
	public static void setUp() throws Exception {
		// 启动hsqldb。本demo使用hsqldb存储流程实例数据
		HsqldbManager.startupHsqldb();

		resource = new ClassPathResource(springConfigFile);
		beanFactory = new XmlBeanFactory(resource);
		transactionTemplate = (TransactionTemplate) beanFactory
				.getBean("transactionTemplate");
		runtimeContext = (RuntimeContext) beanFactory.getBean("runtimeContext");

	}

	public static void tearDown() throws Exception {
		// 启动hsqldb。本demo使用hsqldb存储流程实例数据
		HsqldbManager.stopHsqldb();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// 启动数据库，初始化环境
			setUp();

			//启动流程实例，并执行该实例
			System.out.println("===========启动流程实例，并执行.........");
			transactionTemplate.execute(new TransactionCallback() {

				public Object doInTransaction(TransactionStatus arg0) {
					IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
					try {
						IProcessInstance processInstance = workflowSession.createProcessInstance("TestToolTaskProcess","test user");
						processInstance.run();
					} catch (EngineException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (KernelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return null;
				}

			});
			System.out.println("===========流程实例结束!");
			//关闭数据库
			tearDown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
