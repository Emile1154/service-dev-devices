package ru.emiljan.servicedevdevices.services.notify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.emiljan.servicedevdevices.models.Notify;
import ru.emiljan.servicedevdevices.models.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NotifyBuilderTest {

    @Autowired
    private NotifyBuilder builder;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                    .nickname("test")
                    .password("pwd")
                    .email("test@test.ru")
                    .phone("+7(999)999-99-99")
                .build();
    }

    @Test
    void buildNotify() {
        String key = "welcome";

        Notify notify = builder.buildNotify(key, user, null);

        Assertions.assertNotNull(notify.getTitle());
        Assertions.assertNotNull(notify.getMessage());
        Assertions.assertFalse(notify.isRead());
        Assertions.assertEquals(user, notify.getUser());
    }
}