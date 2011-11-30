package org.fireflow.util;

import org.springframework.security.providers.encoding.Md5PasswordEncoder;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Md5PasswordEncoder md5Encoder = new Md5PasswordEncoder();
		String pwd = md5Encoder.encodePassword("123456", null);	
		System.out.println(pwd);
	}

}
