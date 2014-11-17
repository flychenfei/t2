package com.britesnow.samplesocial.entity;

public class User extends BaseEntity<Long> {

	private String username;
	private String password;
	private String google_access_token;

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

	public String getGoogle_access_token() {
		return google_access_token;
	}

	public void setGoogle_access_token(String google_access_token) {
		this.google_access_token = google_access_token;
	}

	// --------- /Persistent Properties --------- //

	public String toString() {
		return "[" + getId() + ": " + username + "]";
	}

}
