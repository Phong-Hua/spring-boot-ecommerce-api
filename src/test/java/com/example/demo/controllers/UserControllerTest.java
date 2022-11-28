package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.demo.exceptions.UserException;
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
	
	private User initSampleUser() {
		User u = new User();
		u.setId(1L);
		u.setUsername("mockUser");
		u.setPassword("myHashedPassword");
		return u;
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
	
	@Test(expected = UserException.class)
	public void shouldNot_createUser_passwordDoNotMatch() throws Exception {
		
		CreateUserRequest r = new CreateUserRequest();
		r.setUsername("test");
		r.setPassword("password");
		r.setConfirmPassword("passwordNotMatch");

		userController.createUser(r);
	}
	
	@Test(expected = UserException.class)
	public void shouldNot_creatUser_userAlreadyExists() throws Exception {
		
		/**
		 * Mock findByUsername method, whenever this method is called
		 * always return new User object.
		 */
		when(userRepo.findByUsername("test")).thenReturn(new User());
		
		CreateUserRequest r = new CreateUserRequest();
		r.setUsername("test");
		r.setPassword("password");
		r.setConfirmPassword("password");
		
		userController.createUser(r);
	}
	
	@Test
	public void should_findById() throws Exception {
		
		/**
		 * Mock findById, return sampleUser.
		 */
		User sampleUser = initSampleUser();
		Optional<User> userOptional = Optional.of(sampleUser);
		when(userRepo.findById(1L)).thenReturn(userOptional);
		
		ResponseEntity<User> response = userController.findById(1L);
		assertNotNull(response);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		
		User u = response.getBody();
		assertNotNull(u);
		
		assertEquals(u.getId(), sampleUser.getId());
		assertEquals(u.getUsername(), sampleUser.getUsername());
		assertEquals(u.getPassword(), sampleUser.getPassword());
	}

	@Test(expected = UserException.class)
	public void shouldNot_findById() throws Exception {
		long id = 1L;
		when(userRepo.findById(id)).thenReturn(Optional.empty());
		userController.findById(id);
	}

	@Test
	public void should_findByUsername() throws Exception {
		User sampleUser = initSampleUser();
		when (userRepo.findByUsername(sampleUser.getUsername())).thenReturn(sampleUser);
		ResponseEntity<User> response = userController.findByUserName(sampleUser.getUsername());

		User u = response.getBody();
		assertEquals(u.getUsername(), sampleUser.getUsername());
		assertEquals(u.getPassword(), sampleUser.getPassword());
		assertEquals(u.getId(), sampleUser.getId());
	}

	@Test(expected = UserException.class)
	public void shouldnot_findByUsername() throws Exception {
		String username = "mockuser";
		when (userRepo.findByUsername(username)).thenReturn(null);
		ResponseEntity<User> response = userController.findByUserName(username);
	}
}
