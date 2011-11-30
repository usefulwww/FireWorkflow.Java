package org.fireflow.engine.test.support;

import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IFireWorkflowHelperDao;
import org.fireflow.engine.persistence.IPersistenceService;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 继承此类的所有单元测试，每个方法的测试数据都是单独的，都是不互相影响的。
 * 初始化时都是空数据库
 * @author lifw555@gmail.com
 */
@SuppressWarnings("unused")
// hibernate
@ContextConfiguration(locations = { "classpath:/config/applicationContext-hibernate.xml",
									"classpath:/config/FireflowContext-hibernate.xml", 
									"classpath:/config/AllTheProcessHandlers.xml" })
//// jdbc
// @ContextConfiguration(locations = {"classpath:/config/applicationContext-jdbc.xml",
// 									"classpath:/config/FireflowContext-jdbc.xml",
// 									"classpath:/config/AllTheProcessHandlers.xml" })
// JPA
// @ContextConfiguration(locations = {"classpath:/config/applicationContext-jpa.xml",
// 									"classpath:/config/FireflowContext-jpa.xml",
// 									"classpath:/config/AllTheProcessHandlers.xml" })
									
//如果想将执行的结果存储到数据中，那么继承如下这个基类
//public class FireFlowAbstractTests extends AbstractJUnit4SpringContextTests
public class FireFlowAbstractTests extends AbstractTransactionalJUnit4SpringContextTests
{
	private static final Logger log = LoggerFactory.getLogger(FireFlowAbstractTests.class);
	
	@Autowired
	protected RuntimeContext runtimeContext = null;

	@Autowired
	protected TransactionTemplate transactionTemplate = null;
	
	@Autowired
	protected IPersistenceService persistenceService = null;

	@Autowired
	protected IFireWorkflowHelperDao fireWorkflowHelperDao;

	@Before
	public void prepareTestData()
	{
		log.debug("方法执行前调用初始化测试数据");
		//setSqlScriptEncoding("UTF-8");
		//executeSqlScript("classpath:/sql/data4test.sql", false);
	}

	@After
	public void destroyTestData()
	{
		// 每个测试方法执行完成后调用
		log.debug("方法执行后调用");
	}
	
	/**
	 * 刷新session中的实体，使之能够在事务内级别受到方法内前边操作的影响
	 * 只有当继承自AbstractTransactionalJUnit4SpringContextTests.class时，才需要调用refresh这个方法
	 * @param obj
	 */
	protected void refresh(Object obj)
	{
		String thisName = this.getClass().getSuperclass().getSuperclass().getName();
		String superName = AbstractTransactionalJUnit4SpringContextTests.class.getName();
		if(thisName.equals(superName))
		{
			if(fireWorkflowHelperDao instanceof HibernateDaoSupport)
			{
				((HibernateDaoSupport) fireWorkflowHelperDao).getSessionFactory().getCurrentSession().refresh(obj);
			}
			else if(fireWorkflowHelperDao instanceof JdbcDaoSupport)
			{
				//jdbc方式实现，什么也不需要做
			}
		}
	}
	
}
