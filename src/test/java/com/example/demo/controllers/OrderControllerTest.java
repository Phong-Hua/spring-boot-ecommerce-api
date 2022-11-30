package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exceptions.APIBadRequestException;
import com.example.demo.exceptions.APINotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepo = mock(UserRepository.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    private Cart createNonEmptyCart() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(new Item(1L, "item 1", BigDecimal.valueOf(10.95), "Item 1 description"));
        return cart;
    }

    @Test
    /**
     * Should submit order with cart is not empty.
     */
    public void should_submit() {

        // create a user with non-empty cart
        Cart cart = createNonEmptyCart();
        User user = new User(1L, "testuser", "hashedPassword", cart);
        cart.setUser(user);
        List<Item> cartItems = cart.getItems();
        BigDecimal cartTotal = cart.getTotal();

        // mock function findByUsername
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);

        // execute the method
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        UserOrder order = response.getBody();

        // verify status
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        // verify items in order match
        assertEquals(order.getItems(), cartItems);

        // verify the total match
        assertEquals(order.getTotal().compareTo(cartTotal), 0);

        // verify cart is empty after order is made and total is 0
        assertTrue(cart.getItems().isEmpty());
        assertEquals(cart.getTotal(), BigDecimal.ZERO);
    }

    @Test(expected = APINotFoundException.class)
    public void shouldNot_submit_usernameNotFound() {

        // create a user with non empty cart
        Cart cart = createNonEmptyCart();
        User user = new User(1L, "testuser", "hashedPassword", cart);
        cart.setUser(user);

        // mock function findByUsername
        when(userRepo.findByUsername(user.getUsername())).thenReturn(null);
        orderController.submit(user.getUsername());
    }

    @Test(expected = APIBadRequestException.class)
    public void shouldNot_submit_cartIsEmpty() {

        // create a user with empty cart
        User user = new User(1L, "testuser", "hashedPassword", new Cart());

        // mock function: findByUsername
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);

        // call the method
        orderController.submit(user.getUsername());
    }

    @Test
    /**
     * Test for user has not made any order yet.
     */
    public void should_getOrdersForUser_emptyOrderHistory() {

        // create user
        User user = new User(1L, "testuser", "hashedPassword", new Cart());

        // mock function findByUsername, findByUser
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(new ArrayList<>());

        // call method
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

        // assert status 200
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        // assert order is empty
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    /**
     * Test for user who has already made at least one order.
     */
    public void should_getOrdersForUser_noneEmptyOrderHistory() {

        // create user
        User user = new User(1L, "testuser", "hashedPassword", new Cart());

        // create nonempty order
        Cart cart = createNonEmptyCart();

        UserOrder order = UserOrder.createFromCart(cart);

        // mock function findByUsername, findByUser
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(Arrays.asList(order));

        // call the method
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

        // assert order is not empty
        assertEquals(response.getBody().get(0), order);
    }

    @Test(expected = APINotFoundException.class)
    public void shouldNot_getOrdersForUser_usernameNotFound() {

        // Create a new user
        User user = new User(1L, "testuser", "hashedPassword", new Cart());

        // mock function findByUsername
        when(userRepo.findByUsername(user.getUsername())).thenReturn(null);

        // call the method
        orderController.getOrdersForUser(user.getUsername());
    }
}
