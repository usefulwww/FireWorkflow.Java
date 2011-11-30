package org.fireflow.security.persistence;

/**
 * RoleResource entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class RoleResource extends AbstractRoleResource implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public RoleResource() {
	}

	/** full constructor */
	public RoleResource(String roleCode, String resourceCode) {
		super(roleCode, resourceCode);
	}

}
