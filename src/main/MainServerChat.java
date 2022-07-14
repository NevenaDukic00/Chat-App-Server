package main;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import controller.UserController;

public class MainServerChat {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(8888);
			while (true) {
				System.out.println("SERVER!");
				Socket socket = serverSocket.accept();
				UserController userController = new UserController(socket);
				new Thread(userController).start();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
