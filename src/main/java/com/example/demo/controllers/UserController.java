package com.example.demo.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.UserException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		if (!optionalUser.isPresent())
			throw new UserException("User with id not found - " + id);
		return ResponseEntity.ok(optionalUser.get());
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UserException("User with username not found - " + username);
		}
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		
		boolean exist = userRepository.findByUsername(createUserRequest.getUsername()) != null;
		if (exist) {
			log.error("User already exists.");
			throw new UserException("User already exists.");
		}
		if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.error("Passwords do not match.");
			throw new UserException("Passwords do not match.");
		}
		
		User user = new User();

		user.setUsername(createUserRequest.getUsername());
		user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		userRepository.save(user);
		return ResponseEntity.ok(user);
	}
}
