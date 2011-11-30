package org.fireflow.security.persistence;

/**
 * UserRole entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class UserRole extends AbstractUserRole implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public UserRole() {
	}

	/** full constructor */
	public UserRole(String loginid, String roleCode) {
		super(loginid, roleCode);
	}

}
