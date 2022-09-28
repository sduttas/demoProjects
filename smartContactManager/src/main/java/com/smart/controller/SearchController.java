package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ContactRepository contactRepo;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> searchContacts(@PathVariable("query") String query, Principal principal){
		User currentUser = userRepo.getUserByUserName(principal.getName());
		System.out.println(query);
		List<Contact> contacts = contactRepo.findByNameContainingAndUser(query, currentUser);
		System.out.println(contacts.size());
		return ResponseEntity.ok(contacts);
	}
}
