package org.fireflow.security.persistence;

/**
 * AbstractUserRole entity provides the base persistence definition of the
 * UserRole entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractUserRole implements java.io.Serializable {

	// Fields

	private String id;
	private String loginid;
	private String roleCode;

	// Constructors

	/** default constructor */
	public AbstractUserRole() {
	}

	/** full constructor */
	public AbstractUserRole(String loginid, String roleCode) {
		this.loginid = loginid;
		this.roleCode = roleCode;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoginid() {
		return this.loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

}