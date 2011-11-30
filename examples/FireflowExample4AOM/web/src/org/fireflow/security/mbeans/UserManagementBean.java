package org.fireflow.security.mbeans;

import java.util.List;

import org.fireflow.BasicManagedBean;
import org.fireflow.security.persistence.UserDAO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

public class UserManagementBean extends BasicManagedBean{
	//doBizOperQuery查询条件
	String departmentCode4Query = null;
	String loginid4Query = null;

	UserDAO userDAO = null;
	
	List bizDataList = null;


	public String getDepartmentCode4Query() {
		return departmentCode4Query;
	}

	public void setDepartmentCode4Query(String departmentCode4Query) {
		this.departmentCode4Query = departmentCode4Query;
	}

	public String getLoginid4Query() {
		return loginid4Query;
	}

	public void setLoginid4Query(String loginid4Query) {
		this.loginid4Query = loginid4Query;
	}

	
	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	//***********************业务逻辑*******************************/
	/**
	 * 业务查询
	 */
	protected String executeBizOperQuery() {
		Criterion exp1 = Expression.like("departmentName", "%"+this.departmentCode4Query+"%");
		Criterion exp2 = Expression.like("name", "%"+this.loginid4Query+"%");
		Criterion exp3 = Expression.and(exp1, exp2);
		this.bizDataList = userDAO.findUserByCriteria(exp3);
		return null;
	}
	protected String fireBizDataSelected() {
		//super.fireBizDataSelected();
		//this.outcome="UserEditPanel";
		return null;
	}
}
