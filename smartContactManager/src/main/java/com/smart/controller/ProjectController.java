package com.smart.controller;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class ProjectController {
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping("/about")
	public String aboutPage(Model model) {

		model.addAttribute("title", "About - Smart contact manager");

		return "about";
	}

	@GetMapping("/")
	public String homePage(Model model) {

		model.addAttribute("title", "Home - Smart contact manager");

		return "home";
	}

	@GetMapping("/signup")
	public String signup(Model model) {

		model.addAttribute("title", "Signup - Smart contact manager");
		model.addAttribute("user", new User());

		return "signup";
	}
	
	@GetMapping("/signin")
	public String login(Model model) {

		model.addAttribute("title", "Login - Smart contact manager");

		return "login";
	}
	
	@RequestMapping("/login-error")
	public String loginError(Model model) {
		
		model.addAttribute("title", "LoginError - Smart contact manager");

		return "login-error";
	}
	
	@SuppressWarnings("finally")
	@RequestMapping(value="/register_user", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, @RequestParam(value="agree", defaultValue = "false") Boolean agreed, Model model, BindingResult bindResult, HttpSession session) {
		
		try {
			if(!agreed) {
				System.out.println("Terms and conditions not checked !!");
				throw new Exception("Terms and conditions not checked !!");
			}
			
			if(bindResult.hasErrors()) {
				System.out.println("ERRORS: "+bindResult.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageURL("abcd.com");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			User savedResult = userRepo.save(user);
			
			System.out.println("Successfully saved!!");
			model.addAttribute("user", savedResult);
			session.setAttribute("message", new Message("Successfully saved!!", "alert-success"));
		}
		catch(DataIntegrityViolationException SQLe) {
			System.out.println("Duplicate email not allowed!!");
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Email ID already exists!!", "alert-danger"));
		}
		catch(Exception e) {
			e.printStackTrace();
			
			System.out.println("Exception occurred...");
			model.addAttribute("user", user);
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
		}
		finally {
			return "signup";
		}
		
	}
	
}
