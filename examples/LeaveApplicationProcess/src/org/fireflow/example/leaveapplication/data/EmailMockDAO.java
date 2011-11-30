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

public class EmailMockDAO extends HibernateDaoSupport {
	public void save(EmailMock transientInstance) {
		try {
			getHibernateTemplate().save(transientInstance);
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	public List findByUserId(final String userId){
		List result = (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			@Override
			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				Criteria cr = arg0.createCriteria(EmailMock.class);
				cr.add(Expression.eq("userId", userId));
				cr.addOrder(Order.desc("createTime"));
				return cr.list();
			}
			
		});
		
		return result;		
	}
}
