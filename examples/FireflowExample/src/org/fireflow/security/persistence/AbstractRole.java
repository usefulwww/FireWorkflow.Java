package org.fireflow.security.persistence;

/**
 * AbstractRole entity provides the base persistence definition of the Role
 * entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractRole implements java.io.Serializable {

	// Fields

	private String id;
	private String code;
	private String name;
	private String description;
	private String departmentCode;
	private String departmentName ;
	// Constructors

	/** default constructor */
	public AbstractRole() {
	}

	/** minimal constructor */
	public AbstractRole(String code, String name) {
		this.code = code;
		this.name = name;
	}

	/** full constructor */
	public AbstractRole(String code, String name, String description) {
		this.code = code;
		this.name = name;
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

}