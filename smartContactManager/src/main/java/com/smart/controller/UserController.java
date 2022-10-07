package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import javax.swing.text.DefaultEditorKit.CopyAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	BCryptPasswordEncoder bCrypt;
	@Autowired
	UserRepository userRepo;
	@Autowired
	ContactRepository contactRepo;
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String emailID = principal.getName();
		User user = userRepo.getUserByUserName(emailID);
		
		model.addAttribute("title", "Login - Smart contact manager");
		model.addAttribute("name", user.getName());
	}
	
	@RequestMapping("/dashboard")
	public String user_dashboard(Model model) {
		model.addAttribute("title", "Dashboard - Smart contact manager");
		return "normalUser/dashboard";
	}
	
	@RequestMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact - Smart contact manager");
		model.addAttribute("contact", new Contact());
		return "normalUser/addContactForm";
	}
	
	@RequestMapping("/{cid}/contact-details")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) throws IOException {
		try {
			Optional<Contact> contactOptional = this.contactRepo.findById(cid);
			Contact contact = contactOptional.get();
			
			File saveFile = new ClassPathResource("/static/image").getFile();
			String absolutePath = saveFile.getAbsolutePath();
			
			String userEmail = principal.getName();
			User user = userRepo.getUserByUserName(userEmail);
			
			if(user.getId()==contact.getUser().getId()) {
				model.addAttribute("contact",contact);
				model.addAttribute("path",absolutePath+File.separator);
			}
			
			System.out.println(absolutePath);
			System.out.println(contact.getImage());
		}
		catch(NoSuchElementException noE) {
			model.addAttribute("notFound","No such resource found!!");
		}
		
		return "normalUser/contactDetails";
	}
	
	@GetMapping("/view-contact/{page}")
	public String viewContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		
		String UserEmail = principal.getName();
		User user = userRepo.getUserByUserName(UserEmail);
		
		Pageable pageable = PageRequest.of(page,5);
		
		Page<Contact> contactList = contactRepo.findContactsByUserId(user.getId(), pageable);
		
		System.out.println(contactList.getSize());
		contactList.forEach(e->{
			System.out.println(e.getName());
		});
		System.out.println(contactList);
		
		model.addAttribute("title", "View Contact - Smart contact manager");
		model.addAttribute("contactList", contactList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contactList.getTotalPages());
		
		
		return "normalUser/viewContacts";
	}
	
	@SuppressWarnings("deprecation")
	@GetMapping("/delete-contact/{cid}/{page}")
	public String deleteContact(@PathVariable("cid") Integer cid, @PathVariable("page") Integer currPage, Model model, HttpSession session, Principal principal) throws Exception {
		System.out.println(cid);
		System.out.println(currPage);
		try {
			Contact contact = this.contactRepo.getById(cid);
			
			String userEmail = principal.getName();
			User user = this.userRepo.getUserByUserName(userEmail);
			
			if(contact.getUser().getId()==user.getId()) {
				this.contactRepo.deleteById(cid);
			}
			else {
				throw new Exception("Not authorised to delete this contact!!");
			}
			
		}
		catch(EntityNotFoundException noData) {
			session.setAttribute("message", new Message("No such contact available!!","danger"));
		}
		catch(Exception e) {
			session.setAttribute("message", new Message(e.getMessage(),"danger"));
			e.printStackTrace();
		}
		
		return "redirect:/user/view-contact/"+currPage;
	}
	
	@RequestMapping(value="/process-update", method=RequestMethod.POST)
	public String processUpdate(@ModelAttribute Contact contact, @RequestParam("profilePicture") MultipartFile file, @RequestParam("contactID") Integer contactID, Principal principal, HttpSession session) {
		System.out.println("this is the contact id: "+contactID);
		try {
			Contact contactExisting = this.contactRepo.findById(contactID).get();
			User user = this.userRepo.getUserByUserName(principal.getName());
			
			if(!(user.getId()==contactExisting.getUser().getId())) {
				throw new Exception("You are not authorised to update this contact");
			}
			
			String currentImage = contactExisting.getImage();
			contactExisting=contact;
			contactExisting.setUser(user);
			if(!file.isEmpty()) {
				contactExisting.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("/static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			else {
				contactExisting.setImage(currentImage);
			}
			
			contactExisting.setCid(contactID);
			this.contactRepo.save(contactExisting);
			
		}
		catch(EntityNotFoundException noData) {
			session.setAttribute("message", new Message("No such contact available!!","danger"));
		}
		catch(Exception e) {
			session.setAttribute("message", new Message(e.getMessage(),"danger"));
			e.printStackTrace();
		}
		
		return "redirect:/user/"+contactID+"/contact-details";
	}
	
	@SuppressWarnings("deprecation")
	@GetMapping("/update-contact/{cid}/{page}")
	public String updateContact(@PathVariable("cid") Integer cid, @PathVariable("page") Integer currPage, Model model, HttpSession session, Principal principal) throws Exception {
		System.out.println(cid);
		System.out.println(currPage);
		try {
			Contact contact = this.contactRepo.getById(cid);
			
			String userEmail = principal.getName();
			User user = this.userRepo.getUserByUserName(userEmail);
			
			if(contact.getUser().getId()==user.getId()) {
				model.addAttribute("contact", contact);
			}
			else {
				throw new Exception("Not authorised to update this contact!!");
			}
			
		}
		catch(EntityNotFoundException noData) {
			session.setAttribute("message", new Message("No such contact available!!","danger"));
		}
		catch(Exception e) {
			session.setAttribute("message", new Message(e.getMessage(),"danger"));
			e.printStackTrace();
		}
		
		return "normalUser/updateContact";
	}
	
	@SuppressWarnings("finally")
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profilePicture") MultipartFile file,Model model, Principal principal,HttpSession session) {
		try {
			String email = principal.getName();
			System.out.println(email);
			User user = userRepo.getUserByUserName(email);
			
			File saveFile = new ClassPathResource("/static/image").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			if(file.isEmpty()) {
				System.out.println("File is not sent from the frontend");
				contact.setImage("defaultProfileImage.png");
			}
			else {
				contact.setImage(file.getOriginalFilename());
				
				System.out.println("Absolute file path: "+ saveFile.getAbsolutePath());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			
			contact.setUser(user);
			user.getContactList().add(contact);
			System.out.println(user.getEmail());
			this.userRepo.save(user);
			
			System.out.println(contact);
			session.setAttribute("message", new Message("Contact has been saved successfully!!","success"));
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong!!","danger"));
		}
		finally {
			return "normalUser/addContactForm";
		}
	}
	
	@GetMapping("/settings-page")
	public String getSettings(Model model) {
		model.addAttribute("title", "Settings page");
		return "normalUser/settingsPage";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		
		String userEmail = principal.getName();
		User user = this.userRepo.getUserByUserName(userEmail);
		String oldEncodedPassword = user.getPassword();
		
		if(this.bCrypt.matches(oldPassword, oldEncodedPassword)) {
			user.setPassword(this.bCrypt.encode(newPassword));
			this.userRepo.save(user);
			session.setAttribute("message", new Message("Password changed successfully.","success"));
		}
		else {
			session.setAttribute("message", new Message("Password doesn't match.","danger"));
			return "normalUser/settingsPage";
		}
		
		return "redirect:/user/dashboard";
	}
}
