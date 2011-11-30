package org.fireflow.example.goods_deliver_process.persistence;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * A data access object (DAO) providing persistence and search support for Demo
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see org.fireflow.example.goods_deliver_process.persistence.TradeInfo
 * @author MyEclipse Persistence Tools
 */

public class TradeInfoDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(TradeInfoDAO.class);
	// property constants
	public static final String SN = "sn";
	public static final String GOODS_NAME = "goodsName";
	public static final String GOODS_TYPE = "goodsType";
	public static final String QUANTITY = "quantity";
	public static final String UNIT_PRICE = "unitPrice";
	public static final String AMOUNT = "amount";
	public static final String CUSTOMER_NAME = "customerName";
	public static final String CUSTOMER_MOBILE = "customerMobile";
	public static final String CUSTOMER_PHONE_FAX = "customerPhoneFax";
	public static final String CUSTOMER_ADDRESS = "customerAddress";
	public static final String STATE = "state";

	protected void initDao() {
		// do nothing
	}

	public void save(TradeInfo transientInstance) {
		log.debug("saving Demo instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(TradeInfo persistentInstance) {
		log.debug("deleting Demo instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TradeInfo findById(java.lang.String id) {
		log.debug("getting Demo instance with id: " + id);
		try {
			TradeInfo instance = (TradeInfo) getHibernateTemplate().get(
					"org.fireflow.example.data.Demo", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(TradeInfo instance) {
		log.debug("finding Demo instance by example");
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
		log.debug("finding Demo instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Demo as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findBySn(Object sn) {
		return findByProperty(SN, sn);
	}

	public List findByGoodsName(Object goodsName) {
		return findByProperty(GOODS_NAME, goodsName);
	}

	public List findByGoodsType(Object goodsType) {
		return findByProperty(GOODS_TYPE, goodsType);
	}

	public List findByQuantity(Object quantity) {
		return findByProperty(QUANTITY, quantity);
	}

	public List findByUnitPrice(Object unitPrice) {
		return findByProperty(UNIT_PRICE, unitPrice);
	}

	public List findByAmount(Object amount) {
		return findByProperty(AMOUNT, amount);
	}

	public List findByCustomerName(Object customerName) {
		return findByProperty(CUSTOMER_NAME, customerName);
	}

	public List findByCustomerMobile(Object customerMobile) {
		return findByProperty(CUSTOMER_MOBILE, customerMobile);
	}

	public List findByCustomerPhoneFax(Object customerPhoneFax) {
		return findByProperty(CUSTOMER_PHONE_FAX, customerPhoneFax);
	}

	public List findByCustomerAddress(Object customerAddress) {
		return findByProperty(CUSTOMER_ADDRESS, customerAddress);
	}

	public List findByState(Object state) {
		return findByProperty(STATE, state);
	}

	public List findAll() {
		log.debug("finding all Demo instances");
		try {
			String queryString = "from Demo";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public TradeInfo merge(TradeInfo detachedInstance) {
		log.debug("merging Demo instance");
		try {
			TradeInfo result = (TradeInfo) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(TradeInfo instance) {
		log.debug("attaching dirty Demo instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TradeInfo instance) {
		log.debug("attaching clean Demo instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static TradeInfoDAO getFromApplicationContext(ApplicationContext ctx) {
		return (TradeInfoDAO) ctx.getBean("DemoDAO");
	}
}
