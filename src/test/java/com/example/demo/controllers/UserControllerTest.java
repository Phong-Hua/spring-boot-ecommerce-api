package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

public class UserControllerTest {

	private UserController userController;
	
	// Mock object of UserRepository
	private UserRepository userRepo = mock(UserRepository.class);
	
	// Mock object of CartRepository
	private CartRepository cartRepo = mock(CartRepository.class);
	
	// Mock object of BCryptPasswordEncoder
	private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
	
	@Before
	public void setUp() {
		/**
		 * Create UserController object and inject userRepository, cartRepository and passwordEncoder fields 
		 * with the mock objects.
		 */
		userController = new UserController();
		TestUtils.injectObjects(userController, "userRepository", userRepo);
		TestUtils.injectObjects(userController, "cartRepository", cartRepo);
		TestUtils.injectObjects(userController, "passwordEncoder", encoder);

	}
	
	@Test
	public void should_createUser() throws Exception {
		
		/** 
		 * Since encoder is a mock object, whenever the encode method is called with the "pass" parameter, 
		 * it will return "passwordIsHashed" value 
		 */
		when(encoder.encode("pass")).thenReturn("passwordIsHashed");
		
		CreateUserRequest r = new CreateUserRequest();
		r.setUsername("test");
		r.setPassword("pass");
		r.setConfirmPassword("pass");
		
		final ResponseEntity<User> response = userController.createUser(r);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	
		User u = response.getBody();
		
		assertNotNull(u);
		assertEquals(u.getId(), 0);
		assertEquals(u.getUsername(), "test");
		assertEquals(u.getPassword(), "passwordIsHashed");
		
	}
}
