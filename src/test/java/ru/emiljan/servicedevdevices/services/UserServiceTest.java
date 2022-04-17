package ru.emiljan.servicedevdevices.services;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.emiljan.servicedevdevices.models.Role;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.ImageRepository;
import ru.emiljan.servicedevdevices.repositories.RoleRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;
import ru.emiljan.servicedevdevices.services.notify.NotifyService;

import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private NotifyService notifyService;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @MockBean
    private MailSenderService senderService;

    private User user;
    private Role role;

    @BeforeEach
    public void setUp(){
        role = Role.builder().role("TEST").build();
        user = User.builder()
                    .id(99L)
                    .nickname("some_nickname")
                    .password("pwd")
                    .phone("999 999-99-99")
                    .email("test@test.ru")
                    .roles(new HashSet<>(List.of(role)))
                .build();
        System.out.println("LOAD");
    }

    @Test
    void saveUser() {
        Mockito.when(roleRepository.findByRole(anyString())).thenReturn(role);
        this.userService.saveUser(user);
        Mockito.verify(encoder, Mockito.times(1)).encode("pwd");
        Mockito.verify(roleRepository, Mockito.times(1)).findByRole("ROLE_USER");
        Assertions.assertTrue(user.isAccountNonLocked());
        Assertions.assertFalse(user.isActive());
        Assertions.assertNotNull(user.getActivateCode());
        Mockito.verify(imageRepository, Mockito.times(1)).getById(1L);
        Mockito.verify(senderService, Mockito.times(1))
                .send(
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void findUserByEmail() {
        String email = "test@test.ru";
        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(user);
        final User f = userService.findUserByEmail(email);
        Assertions.assertEquals(email,f.getEmail());
    }

    @Test
    void findUserByNickname(){
        String nickname = "some_nickname";
        Mockito.when(userRepository.findByNickname(anyString())).thenReturn(user);
        final User f = this.userService.findUserByNickname(nickname);
        Assertions.assertEquals(nickname,f.getNickname());
    }

    @Test
    void findById() {
        Long id = 99L;
        Mockito.when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(user));
        final User f = this.userService.findById(id);
        Assertions.assertEquals(id, user.getId());
    }

    @Test
    void activateUser() {
        user.setActivateCode("code");
        Mockito.when(userRepository.findUserByActivateCode(anyString())).thenReturn(user);
        boolean b = this.userService.activateUser(user.getActivateCode());
        Assertions.assertTrue(b);
        Assertions.assertNull(user.getActivateCode());
        Assertions.assertTrue(user.isActive());
        Mockito.verify(notifyService, Mockito.times(1)).createNotify(
                ArgumentMatchers.eq("welcome"),
                any()
        );
    }

    @Test
    void activateUserFailed(){
        boolean b = this.userService.activateUser("wrong code");
        Assertions.assertFalse(b);
        Assertions.assertFalse(user.isActive());
        Mockito.verify(notifyService, Mockito.times(0)).createNotify(
                ArgumentMatchers.eq("welcome"),
                any()
        );
    }
}