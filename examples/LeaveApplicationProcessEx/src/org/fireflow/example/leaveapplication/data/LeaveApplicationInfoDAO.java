package org.fireflow.example.leaveapplication.data;

import java.sql.SQLException;
import java.util.List;

import org.fireflow.example.leaveapplication.workflowextension.LeaveApplicationTaskInstanceExtension;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class LeaveApplicationInfoDAO extends HibernateDaoSupport {
	public void save(LeaveApplicationInfo transientInstance) {
		try {
			getHibernateTemplate().save(transientInstance);
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	public void update(LeaveApplicationInfo entity){
		try {
			getHibernateTemplate().update(entity);
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	public LeaveApplicationInfo findBySn(final String sn){
		LeaveApplicationInfo result = (LeaveApplicationInfo)this.getHibernateTemplate().execute(new HibernateCallback(){

			@Override
			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				Criteria cr = arg0.createCriteria(LeaveApplicationInfo.class);
				cr.add(Expression.eq("sn", sn));
				return cr.uniqueResult();
			}
			
		});
		
		return result;
	}
	
	/**
	 * 通过申请人的姓名查询TaskInstance。
	 * 该方法同时演示了通过PersistenceService之外的DAO对流程表做复杂查询。
	 * 很多复杂查询PersistenceService是无能为力的。
	 * @param name
	 * @return
	 */
	public List findTaskInstanceListByApplicantName(final String name){
		List result = (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			@Override
			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				Criteria cr = arg0.createCriteria(LeaveApplicationTaskInstanceExtension.class);
				cr.add(Expression.eq("applicant", name));
				return cr.list();
			}
		});
		return result;		
	}
}
