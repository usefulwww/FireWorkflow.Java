package org.fireflow.example.leaveapplication.data;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class LeaveApprovalInfoDAO extends HibernateDaoSupport {
	public List findApprovalInfoBySn(final String sn){
		List result = (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			@Override
			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				Criteria cr = arg0.createCriteria(LeaveApprovalInfo.class);
				cr.add(Expression.eq("sn", sn));
				cr.addOrder(Order.asc("approvalTime"));
				
				return cr.list();
			}
			
		});
		return result;
	}
	
	public void save(LeaveApprovalInfo transientInstance) {
		try {
			getHibernateTemplate().save(transientInstance);
		} catch (RuntimeException re) {
			throw re;
		}
	}	
}
