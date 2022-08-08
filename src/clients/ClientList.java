package clients;

import java.util.ArrayList;

import controller.UserController;

public class ClientList {

	public static ArrayList<UserController>clients = new ArrayList<>();
	
	public static void addClient(UserController u) {
		clients.add(u);
	}
	public static void sendMessage(int id,String message) {
		System.out.println("USAO U SEND MESSAGE");
		for (UserController userController : clients) {
			//pronalazimo klijenta sa zadatim idjem
			System.out.println("TRAZI KLIJENTA!");
			System.out.println(userController.id + "== " + id);
			if (userController.id==id) {
				System.out.println("NASAO ZA KOMUNIKACIJU!");
				System.out.println(userController.id);
				//saljemo poruku tom klijentu
				userController.sendMessage(message);
			}
		}
		
		
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
