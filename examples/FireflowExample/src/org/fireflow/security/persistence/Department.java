package org.fireflow.security.persistence;

/**
 * Department entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class Department extends AbstractDepartment implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public Department() {
	}

	/** minimal constructor */
	public Department(String code, String name, String parentCode) {
		super(code, name, parentCode);
	}

	/** full constructor */
	public Department(String code, String name, String parentCode,
			String description) {
		super(code, name, parentCode, description);
	}

}
