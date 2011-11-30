package org.fireflow.kernel.impl;

// Generated Feb 25, 2008 11:40:06 AM by Hibernate Tools 3.2.0.b9

import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * 
 * Home object for domain model class Token.
 * @see org.fireflow.kenel.impl.Token
 * @author Hibernate Tools
 */
public class TokenHome {
//TODO  mingjie.mj 20091018 这个类的存在有用吗？是否删除它？
	private static final Log log = LogFactory.getLog(TokenHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Token transientInstance) {
		log.debug("persisting Token instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Token instance) {
		log.debug("attaching dirty Token instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Token instance) {
		log.debug("attaching clean Token instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Token persistentInstance) {
		log.debug("deleting Token instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Token merge(Token detachedInstance) {
		log.debug("merging Token instance");
		try {
			Token result = (Token) sessionFactory.getCurrentSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Token findById(java.lang.String id) {
		log.debug("getting Token instance with id: " + id);
		try {
			Token instance = (Token) sessionFactory.getCurrentSession().get(
					"org.fireflow.kenel.impl.Token", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List findByExample(Token instance) {
		log.debug("finding Token instance by example");
		try {
			List results = sessionFactory.getCurrentSession().createCriteria(
					"org.fireflow.kenel.impl.Token").add(
					Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
