package org.fireflow.security.persistence;

/**
 * Role entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class Role extends AbstractRole implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public Role() {
	}

	/** minimal constructor */
	public Role(String code, String name) {
		super(code, name);
	}

	/** full constructor */
	public Role(String code, String name, String description) {
		super(code, name, description);
	}

}
