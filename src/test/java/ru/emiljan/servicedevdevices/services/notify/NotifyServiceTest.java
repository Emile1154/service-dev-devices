package ru.emiljan.servicedevdevices.services.notify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.emiljan.servicedevdevices.models.Notify;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.NotifyRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NotifyServiceTest {

    @Autowired
    private NotifyService notifyService;

    @MockBean
    private NotifyRepository notifyRepository;

    @MockBean
    private NotifyBuilder builder;

    @MockBean
    private UserRepository userRepository;

    private User user;
    private Notify notify;

    @BeforeEach
    void setUp() {
        user = User.builder()
                    .id(99L)
                    .nickname("some_nickname")
                    .password("pwd")
                    .phone("+7(999)999-99-99")
                    .email("test@test.ru")
                .build();
        notify = Notify.builder()
                    .user(user)
                    .isRead(false)
                    .title("test")
                    .message("test message")
                .build();
    }

    @Test
    void createNotify() {
        String key = "info";
        notifyService.createNotify(key,user, null);
        Mockito.verify(builder, Mockito.times(1)).buildNotify
                (
                        ArgumentMatchers.eq(key),
                        ArgumentMatchers.eq(user),
                        ArgumentMatchers.eq(null)
                );
        Mockito.verify(notifyRepository, Mockito.times(1)).save
                (
                        any()
                );
        Mockito.verify(userRepository, Mockito.times(1)).save
                (
                    ArgumentMatchers.eq(user)
                );
    }

    @Test
    void updateNotify() {
        notifyService.updateNotify(notify);
        Assertions.assertTrue(notify.isRead());
        Mockito.verify(notifyRepository, Mockito.times(1)).save(notify);
    }
}