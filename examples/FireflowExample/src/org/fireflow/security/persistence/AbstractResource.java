package org.fireflow.security.persistence;

/**
 * AbstractResource entity provides the base persistence definition of the
 * Resource entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractResource implements java.io.Serializable {

	// Fields

	private String id;
	private String code;
	private String name;
	private String type;
	private String value;
	private String parentCode;
	private Long sort;
	private String menuFlag;

	// Constructors

	/** default constructor */
	public AbstractResource() {
	}

	/** minimal constructor */
	public AbstractResource(String code, String name, String type,
			String parentCode) {
		this.code = code;
		this.name = name;
		this.type = type;
		this.parentCode = parentCode;
	}

	/** full constructor */
	public AbstractResource(String code, String name, String type,
			String value, String parentCode, Long sort, String menuFlag) {
		this.code = code;
		this.name = name;
		this.type = type;
		this.value = value;
		this.parentCode = parentCode;
		this.sort = sort;
		this.menuFlag = menuFlag;
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

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getParentCode() {
		return this.parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public Long getSort() {
		return this.sort;
	}

	public void setSort(Long sort) {
		this.sort = sort;
	}

	public String getMenuFlag() {
		return this.menuFlag;
	}

	public void setMenuFlag(String menuFlag) {
		this.menuFlag = menuFlag;
	}

}