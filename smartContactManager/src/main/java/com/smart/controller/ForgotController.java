package com.smart.controller;

import java.util.Random;

import javax.mail.Session;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;

@Controller
public class ForgotController {
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	Random random = new Random(99999);
	
	@RequestMapping("/forgot-password")
	public String forgotPassword() {
		System.out.println("Forgot password controller triggered...");
		return "forgotPasswordPage";
	}
	
	@PostMapping("/send-OTP")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		System.out.println("Send OTP controller triggered...");
		System.out.println("Email found: "+email);
		
		User user = userRepository.getUserByUserName(email);
		if(user==null) {
			session.setAttribute("message", new Message("No such user found for: "+email, "danger"));
			return "redirect:/forgot-password";
		}
		session.removeAttribute("message");
		
		
		int nextInt = random.nextInt(1000000);
		
		System.out.println("Generated random OTP: " + nextInt);
		boolean sendingMailSuccess = emailService.sendmail("OTP", Integer.toString(nextInt), "sduttas2014@gmail.com");
		
		if(sendingMailSuccess) {
			System.out.println("Mail sent successfully...");
		}
		else {
			System.out.println("Mail sending failed...");
		}
		
		
		session.setAttribute("otp", nextInt);
		session.setAttribute("emailID", email);
		System.out.println("Generated random OTP: " + nextInt);
		return "verifyOTP";
	}
	
	@PostMapping("/verify-OTP")
	public String verifyOTP(@RequestParam("otp") Integer OTP, HttpSession session) {
		System.out.println(OTP);
		if(session.getAttribute("otp").equals(OTP)) {
			session.removeAttribute("message");
		}
		else {
			session.setAttribute("message", new Message("OTP is not matching...", "danger"));
			return "verifyOTP";
		}
		
		System.out.println("Verify OTP controller triggered...");
		System.out.println("OTP found: "+OTP);
		
		return "confirmPassword";
	}
	
	@PostMapping("/confirm-newPassword")
	public String confirmNewPassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
		System.out.println("confirm new password controller triggered...");
		System.out.println("newPassword found: "+newPassword);
		
		String email = (String)session.getAttribute("emailID");
		User user = userRepository.getUserByUserName(email);
		if(user==null) {
			session.setAttribute("message", new Message("No such user found for: "+email, "danger"));
			return "redirect:/forgot-password";
		}
		else {
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(user);
		}
		
		
		return "redirect:/signin";
	}
	
}
