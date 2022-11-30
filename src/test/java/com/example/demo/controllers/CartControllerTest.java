package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exceptions.APIBadRequestException;
import com.example.demo.exceptions.APINotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        /**
         * Create cartController object,
         * Inject the mock objects
         */
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    private ModifyCartRequest initModifyRequest(String username, long itemId, int quantity){
        return new ModifyCartRequest(username, itemId, quantity);
    }

    @Test(expected = APINotFoundException.class)
    public void shouldNot_addToCart_usernameNotFound() {
        String username = "testuser";
        when(userRepo.findByUsername(username)).thenReturn(null);
        ModifyCartRequest request = initModifyRequest(username, 1L, 1);
        cartController.addToCart(request);
    }

    @Test(expected = APINotFoundException.class)
    public void shouldNot_addToCart_itemNotFound() {
        String username = "testuser";
        User user = new User(1L, username, "hashedPassword", new Cart());
        when(userRepo.findByUsername(username)).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());
        ModifyCartRequest request = initModifyRequest(username, 1L, 1);
        cartController.addToCart(request);
    }

    @Test
    public void should_addToCart_addToEmptyCart() {

        String username = "testuser";
        Cart cart = new Cart();
        User user = new User(1L, username, "hashedPassword", cart);
        Item item = new Item(1L, "item 1", BigDecimal.valueOf(11.95), "Item 1 description");
        cart.setId(1L);
        cart.setUser(user);

        ModifyCartRequest request = initModifyRequest(username, 1L, 1);

        // mock function
        when(userRepo.findByUsername(username)).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        // Call the addToCart method
        ResponseEntity<Cart> response = cartController.addToCart(request);
        Cart actual = response.getBody();

        // assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        // verify user
        assertEquals(actual.getUser(), user);
        // verify items in cart
        assertEquals(actual.getItems(), Arrays.asList(item));
        // verify total
        assertEquals(actual.getTotal(), BigDecimal.valueOf(11.95));
    }

    @Test
    public void should_addToCart_addSameItemExistInCart() {

        String username = "testuser";
        Cart cart = new Cart();
        User user = new User(1L, username, "hashedPassword", cart);
        Item item = new Item(1L, "item 1", BigDecimal.valueOf(11.95), "Item 1 description");
        cart.setId(1L);
        cart.setUser(user);
        cart.addItem(item);

        ModifyCartRequest request = initModifyRequest(username, 1L, 1);

        // mock function
        when(userRepo.findByUsername(username)).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        // call the addToCart method
        ResponseEntity<Cart> response = cartController.addToCart(request);
        Cart actual = response.getBody();

        // verify items in cart
        assertEquals(actual.getItems(), Arrays.asList(item, item));

        // verify total
        assertEquals(actual.getTotal().compareTo(BigDecimal.valueOf(11.95*2)), 0);
    }

    @Test
    public void should_addToCart_addDifferentItemExistInCart() {

        String username = "testuser";
        Cart cart = new Cart();
        User user = new User(1L, username, "hashedPassword", cart);
        Item item1 = new Item(1L, "item 1", BigDecimal.valueOf(11.95), "Item 1 description");
        Item item2 = new Item(2L, "item 2", BigDecimal.valueOf(13.90), "Item 2 description");
        cart.setId(1L);
        cart.setUser(user);
        cart.addItem(item1);

        ModifyCartRequest request = initModifyRequest(username, 2L, 1);

        // mock function
        when(userRepo.findByUsername(username)).thenReturn(user);
        when(itemRepo.findById(2L)).thenReturn(Optional.of(item2));

        // call the addToCart method
        ResponseEntity<Cart> response = cartController.addToCart(request);
        Cart actual = response.getBody();

        // verify items in cart
        assertEquals(actual.getItems(), Arrays.asList(item1, item2));

        // verify total
        assertEquals(actual.getTotal().compareTo(BigDecimal.valueOf(11.95 + 13.90)), 0);
    }

    @Test(expected = APINotFoundException.class)
    public void shouldNot_removeFromCart_usernameNotFound() {
        String username = "testuser";
        Item item1 = new Item(1L, "item 1", BigDecimal.valueOf(11.95), "Item 1 description");
        when(userRepo.findByUsername(username)).thenReturn(null);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item1));
        ModifyCartRequest request = initModifyRequest(username, 1L, 1);
        cartController.removeFromCart(request);
    }

    @Test(expected = APINotFoundException.class)
    public void shouldNot_removeFromCart_itemNotFound(){
        String username = "testuser";
        User user = new User(1L, username, "hashedPassword", new Cart());
        Item item1 = new Item(1L, "item 1", BigDecimal.valueOf(11.95), "Item 1 description");
        when(userRepo.findByUsername(username)).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());
        ModifyCartRequest request = initModifyRequest(username, 1L, 1);
        cartController.removeFromCart(request);
    }

    @Test
    public void should_removeFromCart_itemNotInCart() {

        // Create items
        Item item1 = new Item(1L, "item 1", BigDecimal.valueOf(11.95), "Item 1 description");
        Item item2 = new Item(2L, "item 2", BigDecimal.valueOf(13.90), "Item 2 description");

        // create cart with 1st item
        Cart cart = new Cart();
        cart.addItem(item1);
        cart.setId(1L);

        // create user and set user to cart
        User user = new User(1L, "testuser", "hashedPassword", cart);
        cart.setUser(user);

        // mock function: return user, find second item
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepo.findById(2L)).thenReturn(Optional.of(item2));

        // create request to remove item 2
        ModifyCartRequest request = initModifyRequest(user.getUsername(), 2L, 1);

        // make function call
        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        // verify status
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        // verify cart item
        Cart actual = response.getBody();
        assertEquals(actual.getItems(), Arrays.asList(item1));

        // verify the total is correct
        assertEquals(actual.getTotal().compareTo(item1.getPrice()), 0);
    }

    @Test
    public void should_removeFromCart_emptyCart() {

        // Create cart
        Item item1 = new Item(1L, "item 1", BigDecimal.valueOf(11.95), "Item 1 description");
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());

        // create user and add user to cart
        User user = new User(1L, "testuser", "hashedPassword", cart);
        cart.setUser(user);

        // mock function, findByUsername, findById
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item1));

        // create ModifyCartRequest
        ModifyCartRequest request = initModifyRequest(user.getUsername(), 1L, 1);

        // call the removeFromCart method
        ResponseEntity<Cart> response = cartController.removeFromCart(request);
        Cart actual = response.getBody();

        // verify status
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        // verify cart item is empty
        assertTrue(actual.getItems().isEmpty());

        // verify the total is 0
        assertEquals(actual.getTotal().compareTo(BigDecimal.ZERO), 0);
    }

    @Test
    public void should_removeFromCart_quantityIsBiggerThanInCart() {

        // Create cart with one item
        Item templateItem = new Item(1L, "item 1", BigDecimal.valueOf(11.95), "Item 1 description");
        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(templateItem.clone());

        // create user and add user to cart
        User user = new User(1L, "testuser", "hashedPassword", cart);
        cart.setUser(user);

        // mock function, findByUsername, findById
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(templateItem.clone()));

        // create ModifyCartRequest with quantity of 2
        ModifyCartRequest request = initModifyRequest(user.getUsername(), 1L, 2);

        // call the removeFromCart method
        ResponseEntity<Cart> response = cartController.removeFromCart(request);
        Cart actual = response.getBody();

        // verify status
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        // verify cart quantity is 1
        assertEquals(actual.getItems().size(), 0);

        // verify the total is correct
        assertEquals(actual.getTotal().compareTo(BigDecimal.ZERO), 0);
    }

    @Test
    public void should_removeFromCart() {

        // Create cart with two items
        Item templateItem = new Item(1L, "item 1", BigDecimal.valueOf(11.95), "Item 1 description");
        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(templateItem.clone());
        cart.addItem(templateItem.clone());

        // create user and add user to cart
        User user = new User(1L, "testuser", "hashedPassword", cart);
        cart.setUser(user);

        // mock function, findByUsername, findById
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(templateItem.clone()));

        // create ModifyCartRequest
        ModifyCartRequest request = initModifyRequest(user.getUsername(), 1L, 1);

        // call the removeFromCart method
        ResponseEntity<Cart> response = cartController.removeFromCart(request);
        Cart actual = response.getBody();

        // verify status
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        // verify cart quantity is 1
        assertEquals(actual.getItems().size(), 1);

        // verify the total is correct
        assertEquals(actual.getTotal().compareTo(BigDecimal.valueOf(11.95)), 0);
    }
}
