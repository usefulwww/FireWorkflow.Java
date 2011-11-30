package org.fireflow.security.persistence;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for User
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see org.fireflow.security.persistence.User
 * @author MyEclipse Persistence Tools
 */

public class UserDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(UserDAO.class);
	// property constants
	public static final String LOGINID = "loginid";
	public static final String PASSWORD = "password";
	public static final String NAME = "name";
	public static final String DISABLED = "disabled";
	public static final String ACCOUNT_LOCKED = "accountLocked";
	public static final String EMAIL = "email";
	public static final String DEPARTMENT_CODE = "departmentCode";
	public static final String DEPARTMENT_NAME = "departmentName";
	public static final String TITLE = "title";

	protected void initDao() {
		// do nothing
	}

	public void save(User transientInstance) {
		log.debug("saving User instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(User persistentInstance) {
		log.debug("deleting User instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public User findById(java.lang.String id) {
		log.debug("getting User instance with id: " + id);
		try {
			User instance = (User) getHibernateTemplate().get(
					"org.fireflow.security.persistence.User", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(User instance) {
		log.debug("finding User instance by example");
		try {
			List results = getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding User instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from User as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByLoginid(Object loginid) {
		return findByProperty(LOGINID, loginid);
	}

	public List findByPassword(Object password) {
		return findByProperty(PASSWORD, password);
	}

	public List findByName(Object name) {
		return findByProperty(NAME, name);
	}

	public List findByDisabled(Object disabled) {
		return findByProperty(DISABLED, disabled);
	}

	public List findByAccountLocked(Object accountLocked) {
		return findByProperty(ACCOUNT_LOCKED, accountLocked);
	}

	public List findByEmail(Object email) {
		return findByProperty(EMAIL, email);
	}

	public List findByDepartmentCode(Object departmentCode) {
		return findByProperty(DEPARTMENT_CODE, departmentCode);
	}

	public List findByDepartmentName(Object departmentName) {
		return findByProperty(DEPARTMENT_NAME, departmentName);
	}

	public List findByTitle(Object title) {
		return findByProperty(TITLE, title);
	}

	public List findAll() {
		log.debug("finding all User instances");
		try {
			String queryString = "from User";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public User merge(User detachedInstance) {
		log.debug("merging User instance");
		try {
			User result = (User) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(User instance) {
		log.debug("attaching dirty User instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(User instance) {
		log.debug("attaching clean User instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static UserDAO getFromApplicationContext(ApplicationContext ctx) {
		return (UserDAO) ctx.getBean("UserDAO");
	}
	
	//************************add by chen *********************************/
	public User findUserByLoginid(String loginid){
		List<User> users = (List<User>)this.findByLoginid(loginid);
		if (users==null || users.size()==0)return null;
		return users.get(0);
	}
	
	public List<User> findUsersByRoleCode(final String roleCode){
		List<User> users = (List<User>)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				String hql = "Select m From org.fireflow.security.persistence.User m,org.fireflow.security.persistence.UserRole n where m.loginid=n.loginid and n.roleCode=:roleCode";
				Query q = arg0.createQuery(hql);
				q.setString("roleCode", roleCode);
				return q.list();
			}
			
		});
		return users;
	}
	
	public List findUserByCriteria(final Criterion condition){
		List users = (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				Criteria criteria = arg0.createCriteria(User.class); 
				criteria.add(condition);
				return criteria.list();
			}
			
		});
		return users;
	}	
}