package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name="USER")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
//	@NotBlank(message = "Name field should not be empty.")
//	@Size(min = 8, max = 20, message = "Length of the string should be 8 - 20")
	private String name;
	@Column(unique = true)
//	@Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", message = "Email pattern doesn't match.")
	private String email;
	private String password;
	private String imageURL;
	private String role;
//	@AssertTrue(message = "Enabled field should be true.")
	private boolean enabled;
	@Column(length = 500)
	private String about;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
	private List<Contact> contactList = new ArrayList<>();
	
	
	//Getters, Setters, toString(), constructor
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public List<Contact> getContactList() {
		return contactList;
	}

	public void setContactList(List<Contact> contactList) {
		this.contactList = contactList;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", imageURL="
				+ imageURL + ", role=" + role + ", enabled=" + enabled + ", about=" + about + ", contactList="
				+ contactList + "]";
	}

	public User(int id, String name, String email, String password, String imageURL, String role, boolean enabled,
			String about, List<Contact> contactList) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.imageURL = imageURL;
		this.role = role;
		this.enabled = enabled;
		this.about = about;
		this.contactList = contactList;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
