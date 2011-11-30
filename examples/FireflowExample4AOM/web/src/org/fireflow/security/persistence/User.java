package org.fireflow.security.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

/**
 * User entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class User extends AbstractUser implements java.io.Serializable,UserDetails {

	List<Role> roles = null;
	Boolean selected = Boolean.FALSE;
	
	// Constructors

	/** default constructor */
	public User() {
	}

	/** minimal constructor */
	public User(String loginid, String password, String name, Boolean disabled,
			Boolean accountLocked, String departmentCode) {
		super(loginid, password, name, disabled, accountLocked, departmentCode);
	}

	/** full constructor */
	public User(String loginid, String password, String name, Boolean disabled,
			Boolean accountLocked, Date accountExpiredTime,
			Date passwordExpiredTime, String email, String departmentCode,
			String departmentName, String title) {
		super(loginid, password, name, disabled, accountLocked,
				accountExpiredTime, passwordExpiredTime, email, departmentCode,
				departmentName, title);
	}

	public GrantedAuthority[] getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUsername() {
		// TODO Auto-generated method stub
		return this.getLoginid();
	}

	public boolean isAccountNonExpired() {
		Date expiredTime = this.getAccountExpiredTime();
		if (expiredTime==null) return true;
		Date now = new Date();
		return now.before(expiredTime);
	}

	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return !this.getAccountLocked();
	}

	public boolean isCredentialsNonExpired() {
		Date expiredTime = this.getPasswordExpiredTime();
		if (expiredTime==null) return true;
		Date now = new Date();
		return now.before(expiredTime);
	}

	public boolean isEnabled() {
		return !this.getDisabled();
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	
}
