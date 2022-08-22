package controller;

public class User {

	private String ip;
	private int port;
	private String activeContact;
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getActiveContact() {
		return activeContact;
	}
	public void setActiveContact(String activeContact) {
		this.activeContact = activeContact;
	}
	public User(String ip, int port,int id) {
		super();
		this.ip = ip;
		this.port = port;
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
	
}
