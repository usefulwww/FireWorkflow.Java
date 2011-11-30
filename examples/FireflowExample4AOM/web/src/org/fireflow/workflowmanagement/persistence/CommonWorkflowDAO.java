package org.fireflow.workflowmanagement.persistence;

import java.sql.SQLException;
import java.util.List;

import org.fireflow.engine.definition.WorkflowDefinitionInfo;
import org.fireflow.engine.impl.ProcessInstance;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class CommonWorkflowDAO extends HibernateDaoSupport {
	
	/**
	 * 通过流水号查询出业务的任务列表
	 * @param sn
	 * @return
	 */
	public List findWorkItemsForSN(final String sn){
		final String hql = "select wi From org.fireflow.engine.impl.WorkItem as wi ,org.fireflow.example.workflow_ext.MyTaskInstance as taskInst where wi.taskInstance.id=taskInst.id and taskInst.sn=:sn order by wi.createdTime";
		List result = (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				Query query = arg0.createQuery(hql);
				query.setString("sn", sn);
				
				return query.list();
			}
			
		});
		return result;
	}
	
	public List<WorkflowDefinitionInfo> findWorkflowDefinitionInfoByCriteria(final Criterion condition){
		List result = (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				Criteria criteria = arg0.createCriteria(WorkflowDefinitionInfo.class); 
				criteria.add(condition);
				return criteria.list();
			}
			
		});
		return result;
	}
	
	public List<WorkflowDefinitionInfo> findProcessInstanceByCriteria(final Criterion condition){
		List result = (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				Criteria criteria = arg0.createCriteria(ProcessInstance.class); 
				criteria.add(condition);
				return criteria.list();
			}
			
		});
		return result;
	}	
}
