package org.fireflow.security.persistence;

import java.util.Date;

/**
 * AbstractUser entity provides the base persistence definition of the User
 * entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractUser implements java.io.Serializable {

	// Fields

	private String id;
	private String loginid;
	private String password;
	private String name;
	private Boolean disabled;
	private Boolean accountLocked;
	private Date accountExpiredTime;
	private Date passwordExpiredTime;
	private String email;
	private String departmentCode;
	private String departmentName;
	private String title;

	// Constructors

	/** default constructor */
	public AbstractUser() {
	}

	/** minimal constructor */
	public AbstractUser(String loginid, String password, String name,
			Boolean disabled, Boolean accountLocked, String departmentCode) {
		this.loginid = loginid;
		this.password = password;
		this.name = name;
		this.disabled = disabled;
		this.accountLocked = accountLocked;
		this.departmentCode = departmentCode;
	}

	/** full constructor */
	public AbstractUser(String loginid, String password, String name,
			Boolean disabled, Boolean accountLocked, Date accountExpiredTime,
			Date passwordExpiredTime, String email, String departmentCode,
			String departmentName, String title) {
		this.loginid = loginid;
		this.password = password;
		this.name = name;
		this.disabled = disabled;
		this.accountLocked = accountLocked;
		this.accountExpiredTime = accountExpiredTime;
		this.passwordExpiredTime = passwordExpiredTime;
		this.email = email;
		this.departmentCode = departmentCode;
		this.departmentName = departmentName;
		this.title = title;
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

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getDisabled() {
		return this.disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean getAccountLocked() {
		return this.accountLocked;
	}

	public void setAccountLocked(Boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public Date getAccountExpiredTime() {
		return this.accountExpiredTime;
	}

	public void setAccountExpiredTime(Date accountExpiredTime) {
		this.accountExpiredTime = accountExpiredTime;
	}

	public Date getPasswordExpiredTime() {
		return this.passwordExpiredTime;
	}

	public void setPasswordExpiredTime(Date passwordExpiredTime) {
		this.passwordExpiredTime = passwordExpiredTime;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDepartmentCode() {
		return this.departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDepartmentName() {
		return this.departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}