package ru.emiljan.servicedevdevices.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.emiljan.servicedevdevices.models.Role;
import ru.emiljan.servicedevdevices.models.User;

import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MyUserDetailsServiceTest {

    @Autowired
    private MyUserDetailsService detailsService;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp(){
        user = User.builder()
                    .nickname("test")
                    .email("test@test.ru")
                    .password("pwd")
                    .roles(new HashSet<>(List.of(Role.builder().role("TEST").build())))
                    .active(true)
                    .accountNonLocked(true)
                .build();
    }

    @Test
    void loadUserByUsername(){
        Mockito.when(userService.findUserByNickname(anyString())).thenReturn(user);
        UserDetails userDetails = detailsService.loadUserByUsername(user.getNickname());
        Assertions.assertEquals(user.getNickname(),userDetails.getUsername());
        Assertions.assertEquals(user.isActive(), userDetails.isEnabled());
        Assertions.assertEquals(user.isAccountNonLocked(), userDetails.isAccountNonLocked());
    }

    @Test
    void loadUserByEmail(){
        Mockito.when(userService.findUserByEmail(anyString())).thenReturn(user);
        UserDetails userDetails = detailsService.loadUserByUsername(user.getEmail());
        Assertions.assertEquals(user.isActive(), userDetails.isEnabled());
        Assertions.assertEquals(user.isAccountNonLocked(), userDetails.isAccountNonLocked());
    }
}