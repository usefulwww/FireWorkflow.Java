package org.fireflow.example.loan_process.persistence;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A data access object (DAO) providing persistence and search support for
 * LoanInfo entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see org.fireflow.example.loan_process.persistence.LoanInfo
 * @author MyEclipse Persistence Tools
 */

public class LoanInfoDAO extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(LoanInfoDAO.class);
	// property constants
	public static final String SN = "sn";
	public static final String APPLICANT_NAME = "applicantName";
	public static final String APPLICANT_ID = "applicantId";
	public static final String ADDRESS = "address";
	public static final String SALARY = "salary";
	public static final String LOAN_VALUE = "loanValue";
	public static final String RETURN_DATE = "returnDate";
	public static final String LOAN_TELLER = "loanTeller";
	public static final String SALARY_IS_REAL = "salaryIsReal";
	public static final String CREDIT_STATUS = "creditStatus";
	public static final String RISK_EVALUATOR = "riskEvaluator";
	public static final String DECISION = "decision";
	public static final String LEND_MONEY_INFO = "lendMoneyInfo";
	public static final String LEND_MONEY_OFFICER = "lendMoneyOfficer";
	public static final String REJECT_INFO = "rejectInfo";

	protected void initDao() {
		// do nothing
	}

	public void save(LoanInfo transientInstance) {
		log.debug("saving LoanInfo instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(LoanInfo persistentInstance) {
		log.debug("deleting LoanInfo instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public LoanInfo findById(java.lang.String id) {
		log.debug("getting LoanInfo instance with id: " + id);
		try {
			LoanInfo instance = (LoanInfo) getHibernateTemplate().get(
					"org.fireflow.example.loan_process.persistence.LoanInfo",
					id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(LoanInfo instance) {
		log.debug("finding LoanInfo instance by example");
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
		log.debug("finding LoanInfo instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from LoanInfo as model where model."
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

	public List findByApplicantName(Object applicantName) {
		return findByProperty(APPLICANT_NAME, applicantName);
	}

	public List findByApplicantId(Object applicantId) {
		return findByProperty(APPLICANT_ID, applicantId);
	}

	public List findByAddress(Object address) {
		return findByProperty(ADDRESS, address);
	}

	public List findBySalary(Object salary) {
		return findByProperty(SALARY, salary);
	}

	public List findByLoanValue(Object loanValue) {
		return findByProperty(LOAN_VALUE, loanValue);
	}

	public List findByReturnDate(Object returnDate) {
		return findByProperty(RETURN_DATE, returnDate);
	}

	public List findByLoanTeller(Object loanTeller) {
		return findByProperty(LOAN_TELLER, loanTeller);
	}

	public List findBySalaryIsReal(Object salaryIsReal) {
		return findByProperty(SALARY_IS_REAL, salaryIsReal);
	}

	public List findByCreditStatus(Object creditStatus) {
		return findByProperty(CREDIT_STATUS, creditStatus);
	}

	public List findByRiskEvaluator(Object riskEvaluator) {
		return findByProperty(RISK_EVALUATOR, riskEvaluator);
	}

	public List findByDecision(Object decision) {
		return findByProperty(DECISION, decision);
	}

	public List findByLendMoneyInfo(Object lendMoneyInfo) {
		return findByProperty(LEND_MONEY_INFO, lendMoneyInfo);
	}

	public List findByLendMoneyOfficer(Object lendMoneyOfficer) {
		return findByProperty(LEND_MONEY_OFFICER, lendMoneyOfficer);
	}

	public List findByRejectInfo(Object rejectInfo) {
		return findByProperty(REJECT_INFO, rejectInfo);
	}

	public List findAll() {
		log.debug("finding all LoanInfo instances");
		try {
			String queryString = "from LoanInfo";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public LoanInfo merge(LoanInfo detachedInstance) {
		log.debug("merging LoanInfo instance");
		try {
			LoanInfo result = (LoanInfo) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(LoanInfo instance) {
		log.debug("attaching dirty LoanInfo instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(LoanInfo instance) {
		log.debug("attaching clean LoanInfo instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static LoanInfoDAO getFromApplicationContext(ApplicationContext ctx) {
		return (LoanInfoDAO) ctx.getBean("LoanInfoDAO");
	}
}