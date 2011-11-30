package org.fireflow.security.persistence;

/**
 * AbstractRoleResource entity provides the base persistence definition of the
 * RoleResource entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractRoleResource implements java.io.Serializable {

	// Fields

	private String id;
	private String roleCode;
	private String resourceCode;

	// Constructors

	/** default constructor */
	public AbstractRoleResource() {
	}

	/** full constructor */
	public AbstractRoleResource(String roleCode, String resourceCode) {
		this.roleCode = roleCode;
		this.resourceCode = resourceCode;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getResourceCode() {
		return this.resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

}