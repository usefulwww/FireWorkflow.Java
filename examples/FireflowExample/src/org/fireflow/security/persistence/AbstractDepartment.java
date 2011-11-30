package org.fireflow.security.persistence;

/**
 * AbstractDepartment entity provides the base persistence definition of the
 * Department entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractDepartment implements java.io.Serializable {

	// Fields

	private String id;
	private String code;
	private String name;
	private String parentCode;
	private String description;

	// Constructors

	/** default constructor */
	public AbstractDepartment() {
	}

	/** minimal constructor */
	public AbstractDepartment(String code, String name, String parentCode) {
		this.code = code;
		this.name = name;
		this.parentCode = parentCode;
	}

	/** full constructor */
	public AbstractDepartment(String code, String name, String parentCode,
			String description) {
		this.code = code;
		this.name = name;
		this.parentCode = parentCode;
		this.description = description;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentCode() {
		return this.parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}