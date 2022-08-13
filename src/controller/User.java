package controller;

public class User {

	private String ip;
	private int port;
	private String activeContact;
	
	public String getActiveContact() {
		return activeContact;
	}
	public void setActiveContact(String activeContact) {
		this.activeContact = activeContact;
	}
	public User(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
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
