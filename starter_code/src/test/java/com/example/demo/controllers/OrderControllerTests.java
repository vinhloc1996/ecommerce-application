package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class OrderControllerTests {
    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        BigDecimal price = BigDecimal.valueOf(10.98);
        item.setPrice(price);
        item.setDescription("Item 1 Desc");
        List<Item> items = new ArrayList<>();
        items.add(item);

        User user = new User();
        Cart cart = new Cart();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(items);
        BigDecimal total = BigDecimal.valueOf(10.98);
        cart.setTotal(total);
        user.setCart(cart);
        when(userRepository.findByUsername("username")).thenReturn(user);
        when(userRepository.findByUsername("invalidUsername")).thenReturn(null);
    }

    @Test
    public void submit_success() {
        ResponseEntity<UserOrder> response = orderController.submit("username");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        verify(orderRepository , times(1)).save(order);
        verify(userRepository, times(1)).findByUsername("username");
    }

    @Test
    public void submit_invalid_user() {
        ResponseEntity<UserOrder> response = orderController.submit("invalidUsername");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository, times(1)).findByUsername("invalidUsername");
    }

    @Test
    public void getOrdersForUser_success() {
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("username");
        assertNotNull(ordersForUser);
        assertEquals(200, ordersForUser.getStatusCodeValue());
        List<UserOrder> orders = ordersForUser.getBody();
        assertNotNull(orders);
        verify(userRepository, times(1)).findByUsername("username");
    }

    @Test
    public void getOrdersForUser_invalid_user() {
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("invalidUsername");
        assertNotNull(ordersForUser);
        assertEquals(404, ordersForUser.getStatusCodeValue());
        verify(userRepository, times(1)).findByUsername("invalidUsername");
    }
}
