package org.fireflow.security.persistence;

/**
 * Resource entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class Resource extends AbstractResource implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public Resource() {
	}

	/** minimal constructor */
	public Resource(String code, String name, String type, String parentCode) {
		super(code, name, type, parentCode);
	}

	/** full constructor */
	public Resource(String code, String name, String type, String value,
			String parentCode, Long sort, String menuFlag) {
		super(code, name, type, value, parentCode, sort, menuFlag);
	}

}
