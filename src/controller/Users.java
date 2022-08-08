package controller;

import java.util.ArrayList;

public class Users {
	
	public static ArrayList<User>users = new ArrayList<User>();
	
	public static void removeUser(int port) {
		int i = 0;
		for(User user:users) {
			if(user.getPort()==port) {
				System.out.println("Izbrisao je usera!");
				users.remove(i);
				return;
			}
			i++;
		}
	}
	
	public static int isActive(int port) {
		int i = 0;
		for(User user:users) {
			if(user.getPort()==port) {
				return 1;
			}
			i++;
		}
		
		return 0;
	}
}
