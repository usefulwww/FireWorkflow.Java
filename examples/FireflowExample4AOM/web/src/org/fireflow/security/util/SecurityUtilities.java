package org.fireflow.security.util;

import org.fireflow.security.persistence.User;
import org.springframework.security.context.SecurityContextHolder;

public class SecurityUtilities {
	public static User getCurrentUser(){
		return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
