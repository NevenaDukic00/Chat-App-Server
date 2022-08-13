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
	
	public static void setConatct(int id, String email) {
		
		for(User user:users) {
			if(user.getPort()==id) {
				user.setActiveContact(email);
				return;
			}
			
		}
	}
	public static void removeContatc(int port) {
		for(User user:users) {
			if(user.getPort()==port) {
				//postavljamo da je korisnik sa kojim pricamo null
				user.setActiveContact(null);
				return;
			}
			
		}
		
	}
	public static int isActive(String email,int port) {
	
		for(User user:users) {
			
			if(user.getPort()==port && (user.getActiveContact()!=null && user.getActiveContact().equals(email))) {
				return 1;
			}
			
		}
		
		return 0;
	}
}
