package it.polimi.tiw.beans;

public class User {
	private final int id;
	private final String name;
	private final String surname;
	private final String username;
	private	final String address;
	
	public User(int id, String name, String surname, String username, String address) throws RuntimeException {
		if(name == null || surname == null || username == null || address == null) throw new RuntimeException("Fill all the fields");	
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.username = username;
		this.address = address;
	}
	
	public String getName() {
		return this.name;
	}

	public int getId() {
		return id;
	}

	public String getSurname() {
		return surname;
	}

	public String getUsername() {
		return username;
	}

	public String getAddress() {
		return address;
	}
	
	
}