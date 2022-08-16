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
		
		outputStream.writeInt(1);
		if (dataBase.checkUser(email)==0) {
			//ukoliko nema usera sa unetim mailom onda ga pravimo
			dataBase.registration(email, username, password);
			outputStream.writeInt(1);
		}else {
			//ima emaila, saljemo poruku da user vec postoji
			outputStream.writeInt(0);
		}
		outputStream.flush();
		
	}
	private void checkUser() {
		try {
			//uzimamo podatke i proveravamo da li user sa tim emailom i sifrom postoji u bazi
			String email = inputStream.readUTF();
			String password = inputStream.readUTF();
			
			//uzimamo ip adresu
			String ip = inputStream.readUTF();
			
			outputStream.writeInt(2);
			//proveravamo da li user postoji u bazi
			if (dataBase.chechUserSignIn(email, password)==1) {
				
				this.id = dataBase.getId(email);
				
				//proveravamo da li je user vec logovan u aplikaciju
				if(ClientList.checkActiveUser(dataBase.getId(email))==0) {
					//ukoliko nije ovo se izvrsava
					outputStream.writeInt(1);
					//ubacujemo klijenta u listu aktivnih Socketa
					
					ClientList.addClient(UserController.this);
					
					//uzimamo potreban deo ip adrese
					int position = ip.indexOf("/");
					String ip1 = ip.substring(position+1);
					
					//ubacujemo korisnika u listu aktivnih korisnika
					Users.users.add(new User(ip1,dataBase.getId(email)));
					
					//ovde saljemo port korisniku kako bi on pokrenuo Server za p2p komunikaciju
					outputStream.writeInt(dataBase.getId(email));
				}else {
					//ukoliko je korinsik vec ulogovan saljemo 2
					outputStream.writeInt(2);
				}
			}else {
				//ukoliko ne postoji user sa unetim podacima saljemo 0
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
			
			//proveravamo da li chat postoji
			int status = dataBase.addChat(email1, email2);
			
			
			ArrayList<String> messages = new ArrayList<>();
			
			//kako bismo znali koji korisnik prica sa kim u kom trenutku, 
			//u listi aktivnih korisnika dodajemo da se trenutno nalazimo u chatu sa korisnikom sa email1
			Users.setConatct(id, email1);
			outputStream.writeInt(3);
			outputStream.writeUTF(dataBase.getUserName(dataBase.getId(email1)));
			outputStream.writeInt(status);
			if (status==1) {
				//ukoliko chat postoji uzimamo sve zapisane poruke iz njega
				System.out.println("USAO DA CITA PORUKE!");
				messages = dataBase.getMessages(dataBase.getChatId(dataBase.getId(email1),dataBase.getId(email2)));
				
				for (int i = 0; i < messages.size(); i++) {
					outputStream.writeUTF(messages.get(i));
				}
				outputStream.writeUTF("end of messages");
				
				
			}
			outputStream.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendMessage(String message,String user) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					//saljemo username korisnika od koga prima poruku i poruku
					System.out.println("USAO DA SALJE PORUKU");
					outputStream.writeInt(4);
					outputStream.writeUTF(user);
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
			System.out.println("SALJE PORUKU KA BAZI");
			//saljemo to ka bazi
			dataBase.addMessage(email1, email2, message);
			//saljemo poruku drugom klijentu i username klijenta od koga prima poruku, ali prvo uzimo id tog klijenta kome saljemo
			ClientList.sendMessage(dataBase.getId(email2),message,dataBase.getUserName(id));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void checkEmail() {
		
		try {
			//proveravamo postoji li user sa tim emailom
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
			//uzimamo sve kontakte tog usera
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
	
	public void logOff() {
		try {
			//server uklanja korinsika iz liste aktivnih usera
			ClientList.removeUser(this.id);
			Users.removeUser(this.id);
			//vraca povratnu poruku da je primio obavestenje i zatvara streamove i socket za tog korisnika
			outputStream.writeInt(8);
			outputStream.flush();
			socket.close();
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendPort() {
		try {
			
			
			String email = inputStream.readUTF();
			
			//proveravamo da li je korinsik kome saljemo glasovnu poruku na mrezi i u nasem chatu
			int status = Users.isActive(dataBase.getEmail(id),dataBase.getId(email));
			
			outputStream.writeInt(7);
			//ukoliko je korisnik aktivan i u nasem chatu:
			if(status==1) {
				outputStream.writeInt(1);
				//uzimamo korisnikov port
				int port = dataBase.getId(email);
				String ip = "";
				//trazimo ip adresu korisnika
				for(int i = 0;i<Users.users.size();i++) {
					if(Users.users.get(i).getPort()==port) {
						ip = Users.users.get(i).getIp();
						break;
					}
				}
				//saljemo port i ip
				outputStream.writeInt(port);
				outputStream.writeUTF(ip);
			}else {
				outputStream.writeInt(0);
			}
			
			
			
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeContact() {
		
		try {
			//azuriramo da korisnik vise ne komunicira ni sa kim(nije ni u jendom chatu)
			String email = inputStream.readUTF();
			Users.removeContatc(dataBase.getId(email));
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
				case 8:
					logOff();
					break;
				case 10:
					removeContact();
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
