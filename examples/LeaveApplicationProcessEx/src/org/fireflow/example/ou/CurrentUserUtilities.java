package org.fireflow.example.ou;


public class CurrentUserUtilities {
	private static final ThreadLocal currentUser = new ThreadLocal();
	
	public static User getCurrentUser(){
		return (User)currentUser.get();
	}
	
	public static void setCurrentUser(User u){
		currentUser.set(u);
	}
}
