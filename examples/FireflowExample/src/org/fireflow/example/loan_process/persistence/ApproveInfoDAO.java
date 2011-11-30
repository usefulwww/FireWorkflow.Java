package org.fireflow.example.loan_process.persistence;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ApproveInfoDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(LoanInfoDAO.class);

	public void attachDirty(ApproveInfo instance) {
		log.debug("attaching dirty LoanInfo instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public ApproveInfo findBySnAndUserId(final String sn ,final String userId){
		ApproveInfo result = (ApproveInfo)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				String hql = "From ApproveInfo as m Where m.sn=:sn and m.approver=:userId";
				Query q = arg0.createQuery(hql);
				
				q.setString("sn", sn);
				q.setString("userId", userId);
				return q.uniqueResult();
			}
			
		});
		return result;
	}
}
