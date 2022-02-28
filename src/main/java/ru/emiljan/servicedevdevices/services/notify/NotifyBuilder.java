package ru.emiljan.servicedevdevices.services.notify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.emiljan.servicedevdevices.models.Notify;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.services.MailSenderService;

@Component
public class NotifyBuilder {
    private final MailSenderService senderService;
    private final Environment environment;

    private static final String DEFAULT_PROP_KEY = "notifies.ru.";

    @Autowired
    public NotifyBuilder(MailSenderService senderService, Environment environment) {
        this.senderService = senderService;
        this.environment = environment;
    }

    public Notify buildNotify(String key, User user){
        String title = environment.getProperty(DEFAULT_PROP_KEY+key+".title");
        String msg = environment.getProperty(DEFAULT_PROP_KEY+key+".message");

        if(title == null || msg == null){
            System.out.println("ERROR READ PROP!");
            return null;
        }
        if(title.matches("%s")){
            title = String.format(title, user.getNickname());
        }
        if(user.isAccept_notify()){
            senderService.send(user.getEmail(), title, msg);
        }
        return Notify.builder()
                    .title(title)
                    .message(msg)
                    .isRead(false)
                    .user(user)
                .build();

    }
}
