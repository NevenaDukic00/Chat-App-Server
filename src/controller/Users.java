package controller;

import java.util.ArrayList;

public class Users {
	
	public static ArrayList<User>users = new ArrayList<User>();
	
	public static void removeUser(int id) {
		int i = 0;
		for(User user:users) {
			if(user.getId()==id) {
				System.out.println("Izbrisao je usera!");
				users.remove(i);
				return;
			}
			i++;
		}
	}
	
	public static void setConatct(int id, String email) {
		
		for(User user:users) {
			if(user.getId()==id) {
				user.setActiveContact(email);
				return;
			}
			
		}
	}
	public static void removeContatc(int id) {
		for(User user:users) {
			if(user.getId()==id) {
				//postavljamo da je korisnik sa kojim pricamo null
				user.setActiveContact(null);
				return;
			}
			
		}
		
	}
	
	public static void provera() {
		for(User user:users) {
			System.out.println("Ovaj: " + user.getId() + "prica sa:" + user.getActiveContact() + ", nalazi se na portu: " +user.getPort());
		}
	}
	
	public static int isActive(String email,int id) {
		
		provera();
		for(User user:users) {
			System.out.println(user.getId() +"=="+id);
			if(user.getId()==id && (user.getActiveContact()!=null && user.getActiveContact().equals(email))) {
				return 1;
			}
			
		}
		
		return 0;
	}
}
