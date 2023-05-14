package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class UserControllerTests {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

        User user = new User();
        Cart cart = new Cart();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setCart(cart);
        when(userRepository.findByUsername("username")).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(userRepository.findByUsername("invalidUsername")).thenReturn(null);

    }

    @Test
    public void createUser_success() {
        when(bCryptPasswordEncoder.encode("password")).thenReturn("hashedPasswordString");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("username");
        r.setPassword("password");
        r.setConfirmPassword("password");
        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("username", u.getUsername());
        assertEquals("hashedPasswordString", u.getPassword());
        verify(userRepository, times(1)).save(u);
    }

    @Test
    public void createUser_invalid_password() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("username");
        r.setPassword("pass");
        r.setConfirmPassword("pass");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUser_password_invalid_confirm_password() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("username");
        r.setPassword("password");
        r.setConfirmPassword("passwork");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findUserByName_success() {
        final ResponseEntity<User> response = userController.findByUserName("username");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals("username", u.getUsername());
        verify(userRepository, times(1)).findByUsername("username");
    }

    @Test
    public void findUserByName_not_found() {
        final ResponseEntity<User> response = userController.findByUserName("invalidUsername");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository, times(1)).findByUsername("invalidUsername");
    }

    @Test
    public void findUserById_success() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(1L, u.getId());;
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void findUserById_not_found() {
        final ResponseEntity<User> response = userController.findById(2L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository, times(1)).findById(2L);
    }
}
