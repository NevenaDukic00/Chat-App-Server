package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import clients.ClientList;

import database.DataBase;

public class UserController extends Thread {

	private DataOutputStream outputStream;
	private DataInputStream inputStream;
	
	private Socket socket;
	private DataBase dataBase;
	
	public int id;
	
	
	
	public UserController(Socket socket) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		this.socket = socket;
		try {
			outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataBase = new DataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void registration() throws IOException {
		//zelimo da registrujemo korisnika i proveravamo da li vec postoji korisnik sa tim mailom
		//uzimamo podatke
		String email = inputStream.readUTF();
		String username = inputStream.readUTF();
		String password = inputStream.readUTF();
		
		if (dataBase.checkUser(email)==0) {
			//ukoliko nema usera sa unetim mailom onda ga pravimo
			dataBase.registration(email, username, password);
			outputStream.writeInt(1);
			outputStream.writeInt(1);
			
		}else {
			//ima emaila, ne pravimo usera i saljemo poruku da user vec postoji
			outputStream.writeInt(1);
			outputStream.writeInt(0);
		}
		outputStream.flush();
		
	}
	private void checkUser() {
		try {
			//uzimamo podatke i proveravamo da li user sa tim emailom i sifrom postoji u bazi
			String email = inputStream.readUTF();
			String password = inputStream.readUTF();
			
			if (dataBase.chechUserSignIn(email, password)==1) {
				this.id = dataBase.getId(email);
				//ukoliko postoji, dodajemo ga u listu aktivnih klijenatas
				ClientList.addClient(UserController.this);
				//saljemo signal da user postoji
				outputStream.writeInt(2);
				outputStream.writeInt(1);
				System.out.println("PORT ODNOSNO ID JE: " + dataBase.getId(email));
				//ovde saljemo port
				outputStream.writeInt(dataBase.getId(email));
			}else {
				outputStream.writeInt(2);
				outputStream.writeInt(0);
			}
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void addChat() {
		
		try {
			
			String email1 = inputStream.readUTF();
			String email2 = inputStream.readUTF();
			
			int status = dataBase.addChat(email1, email2);
			String [] messages = new String[200];
			
			if (status==1) {
				//ukoliko chat postoji uzimamo sve zapisane poruke iz njega
				messages = dataBase.getMessages(dataBase.getChatId(dataBase.getId(email1),dataBase.getId(email2)));
				outputStream.writeInt(3);
				for (int i = 0; i < messages.length; i++) {
					if (messages[i]==null) {
						break;
					}
					System.out.println("Poruka: " + messages[i]);
					outputStream.writeUTF(messages[i]);
				}
				outputStream.writeUTF("end of messages");
				outputStream.flush();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendMessage(String message) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					//saljemo poruku
					outputStream.writeInt(4);
					outputStream.writeUTF(message);
					outputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}).start();
	}
	private void addMessage() {
		
		try {
			//prihavta email posiljaoca, primaoca i poruku
			String email1 = inputStream.readUTF();
			String email2 = inputStream.readUTF();
			String message = inputStream.readUTF();
			System.out.println("PORUKA JE: " + message);
			//saljemo to ka bazi
			dataBase.addMessage(email1, email2, message);
			//saljemo poruku drugom klijentu, ali prvo uzimo id tog klijenta kome saljemo
			ClientList.sendMessage(dataBase.getId(email2),message);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void checkEmail() {
		
		try {
			//proveravamo posotji li user sa tim emailom
			String email = inputStream.readUTF();
			int status = dataBase.checkEmail(email);
			//saljemo rezultat pretrage
			outputStream.writeInt(5);
			outputStream.writeInt(status);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getContacts() {
		
		try {
			//uzimamo sve kontakte id tog usera
			String email = inputStream.readUTF();
			ArrayList<String> contacts = dataBase.getContacts(email);
			int length = contacts.size();
		
			int k = 0;
			outputStream.writeInt(6);
			outputStream.writeInt(length);
			//saljemo sve kontakte
			while (k<length) {
				outputStream.writeUTF(contacts.get(k));
				k++;
			}
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
			
	public void sendPort() {
		try {
			String email = inputStream.readUTF();
			
			outputStream.writeInt(7);
			outputStream.writeInt(dataBase.getId(email));
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		while (true) {
			try {
				
				int message = inputStream.readInt();
				System.out.println("MESSAGE je : " + message);
				switch (message) {
				case 1:
					registration();
					break;
				case 2:
					checkUser();
					break;
				case 3:
					addChat();
					break;
				case 4:
					addMessage();
					break;
				case 5:
					checkEmail();
					break;
				case 6:
					getContacts();
					break;
				case 7:
					sendPort();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			super.run();
		}
		
	}
	
}
