package database;

import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataBase {

	private static Connection connection = null;
	private static String databaseName = "";
	private static String url = "jdbc:mysql://localhost:3306/" + databaseName;
	
	private static String username = "root";
	private static String password = "root";
	
	private static PreparedStatement preparedStatement;
	
	public DataBase() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int chechUserSignIn(String email,String password) {
		
		//proveravamo da li user postoji
		String sql = "SELECT * from chat.user where email = ? and pswd = ?";
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);
			ResultSet status = preparedStatement.executeQuery();
			
			while (status.next()) {
				System.out.println(status.getString(1));
				return 1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public int checkUser(String email) {
		
		//proveravamo postoji li user sa ovim emailom
		String sql = "SELECT * from chat.user where email = ?";
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, email);
			ResultSet status = preparedStatement.executeQuery();
			
			while (status.next()) {
				System.out.println(status.getString(1));
				return 1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public void registration(String email,String username,String password) {
		
		try {
			//kreiramo usera
			String sql = "INSERT into chat.user(email,username,pswd)values(?,?,?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, username);
			preparedStatement.setString(3, password);
			int status = preparedStatement.executeUpdate();
			if (status!=0) {
				System.out.println("Upisano je!");
				
			}else {
				System.out.println("Greska!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void storeMessage(String message) {
		
	}
	public int getId(String email) {
		
		
		try {
			String sql = "SELECT * from chat.user where email=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, email);
			ResultSet status = preparedStatement.executeQuery();
			while (status.next()) {
				System.out.println("Id usera je: " + status.getInt("id"));
				return status.getInt("id");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	public int getChatId(int id1,int id2) {
		try {
			String sql = "SELECT * from chat.chat where (user1_id=? and user2_id=?) or (user1_id=? and user2_id=?) ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id1);
			preparedStatement.setInt(2, id2);
			preparedStatement.setInt(3, id2);
			preparedStatement.setInt(4, id1);
			ResultSet status = preparedStatement.executeQuery();
			while (status.next()) {
				System.out.println("Id chata je: " + status.getInt("id"));
				return status.getInt("id");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	
	public ArrayList<String> getContacts(String email) {
		
		System.out.println("USAO DA TRAZI CONATCTA!");
		ArrayList<String> contacts = new ArrayList();
		int id = getId(email);
		String sql = "SELECT user1_id,user2_id from chat.chat where user1_id=? or user2_id=? ";
		
		//trazimo u bazi sve redove gde se zadati mail nalazi na prvom ili drugom mestu, odnosno sve kontakte klijenta
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			preparedStatement.setInt(2, id);
			ResultSet status = preparedStatement.executeQuery();
			
			while (status.next()) {
				if (getEmail(status.getInt(1)).equals(email)) {
					contacts.add(getEmail(status.getInt(2)));
				}else {
					contacts.add(getEmail(status.getInt(1)));
				}
			}
			return contacts;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public void addMessage(String email1,String email2,String message) {
		
		int id1 = getId(email1);
		int id2 = getId(email2);
		
		//trazimo chat izmedju navedena dva korisnika
		int chat = getChatId(id1, id2);
		
		try {
			//pronadjenom chatu dodajemo poruku i id usera koji je poslao poruku
			String sql = "INSERT into chat.messages(chat_id,message,id_user)values(?,?,?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1,chat);
			preparedStatement.setString(2, message);
			preparedStatement.setInt(3, id1);
			int status = preparedStatement.executeUpdate();
			if (status!=0) {
				System.out.println("Upisano je!");
			}else {
				System.out.println("Greska!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public int addChat(String email1,String email2) {
		
		try {
			System.out.println("Email1 je: " + email1);
			int id1 = getId(email1);
			System.out.println("Email2 je: " + email2);
			int id2 = getId(email2);
			
			//proveravamo da li chat postoji
			String sql = "SELECT * from chat.chat where (user1_id=? && user2_id = ?) || (user1_id=? && user2_id = ?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id1);
			preparedStatement.setInt(2, id2);
			preparedStatement.setInt(3, id2);
			preparedStatement.setInt(4, id1);
			ResultSet status = preparedStatement.executeQuery();
			if (status.next()) {
				return 1;
			}else {
				//ukoliko chat ne postoji dodajemo ga
				String sql1 = "INSERT into chat.chat(user1_id,user2_id) values(?,?)";
				System.out.println("USAO OVDEEEEEE!");
				preparedStatement = connection.prepareStatement(sql1);
				preparedStatement.setInt(1, id1);
				preparedStatement.setInt(2, id2);
				int status1 = preparedStatement.executeUpdate();
				if (status1!=0) {
					System.out.println("Napravljen chat!");
				}
				return 0;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return 0;	
	}
	public String getEmail(int id) {
		
		try {
			String sql = "SELECT * from chat.user where id=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			ResultSet status = preparedStatement.executeQuery();
			while (status.next()) {
				System.out.println("Id usera je: " + status.getInt("id"));
				return status.getString("email");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public String getUserName(int id) {
		
		try {
			String sql = "SELECT * from chat.user where id=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			ResultSet status = preparedStatement.executeQuery();
			while (status.next()) {
				System.out.println("Id usera je: " + status.getInt("id"));
				return status.getString("username");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public int checkEmail(String email) {
		try {
			//proveravamo postoji li user sa tim emailom
			String sql = "SELECT * from chat.user where email=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1,email);
			ResultSet status = preparedStatement.executeQuery();
			while (status.next()) {
				return 1;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public ArrayList<String> getMessages(int chat_id) {
		
		String sql = "SELECT * from chat.messages where chat_id=?";
		ArrayList<String> messages = new ArrayList<>();
		
		
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, chat_id);
			ResultSet status = preparedStatement.executeQuery();
			while (status.next()) {
				//uzimam poruku u formatu (email korisninka koji je poslao poruku;poruka#username kako bismo znali koji korisnik je sta napisao) i to smestamo u listu
				messages.add(getEmail(status.getInt(4)).toString().concat(";").concat(status.getString(3)).concat("#").concat(getUserName(status.getInt(4))));
				
			}
			return messages;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
