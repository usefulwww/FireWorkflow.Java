package org.fireflow.example.leaveapplication.data;

import java.sql.SQLException;

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
}
