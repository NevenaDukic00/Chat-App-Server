package clients;

import java.util.ArrayList;

import controller.UserController;

public class ClientList {

	public static ArrayList<UserController>clients = new ArrayList<>();
	
	public static void addClient(UserController u) {
		clients.add(u);
	}
	public static void sendMessage(int id,String message,String user) {
		
		//prolazimo kroz listu aktivnih klijenata da vidimo da lije korisnik kome saljemo poruku na mrezi
		for (UserController userController : clients) {
			//pronalazimo klijenta sa zadatim id-jem
			if (userController.id==id) {
				//saljemo poruku tom klijentu
				userController.sendMessage(message,user);
			}
		}
	}
	
	public static int checkActiveUser(int id) {
		for(UserController userController:clients) {
			if(userController.id==id) {
				return 1;
			}
		}
		return 0;
		
	}
	public static void removeUser(int id) {
		
		System.out.println("Ulazi u brisanje!");
		int i = 0;
		for(UserController userController:clients) {
			if(userController.id==id) {
				clients.remove(i);
				System.out.println("Izbrisan!");
				return;
			}
			i++;
		}
		
	}
	
}
