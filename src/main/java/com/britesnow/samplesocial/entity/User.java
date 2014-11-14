package com.britesnow.samplesocial.entity;

public class User extends BaseEntity<Long> {

	private String username;
	private String password;

	// denote if it is an admin user
	private Boolean admin = false;

	public User(){}

	public User(String username, String password){
		setUsername(username);
		setPassword(password);
	}

	// --------- Persistent Properties --------- //
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	// --------- /Persistent Properties --------- //

	public String toString() {
		return "[" + getId() + ": " + username + "]";
	}

}
