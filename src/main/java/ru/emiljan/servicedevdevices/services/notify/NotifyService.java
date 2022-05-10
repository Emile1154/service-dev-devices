package ru.emiljan.servicedevdevices.services.notify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.Notify;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.NotifyRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;

import java.net.URI;
import java.util.List;

/**
 * Service class for {@link ru.emiljan.servicedevdevices.models.Notify}
 *
 * @author EM1LJAN
 */
@Service
public class NotifyService {
    private final NotifyRepository notifyRepository;
    private final NotifyBuilder notifyBuilder;
    private final UserRepository userRepository;

    @Autowired
    public NotifyService(NotifyRepository notifyRepository,
                         NotifyBuilder notifyBuilder,
                         UserRepository userRepository) {
        this.notifyRepository = notifyRepository;
        this.notifyBuilder = notifyBuilder;
        this.userRepository = userRepository;
    }

    /**
     * create new notify method
     * @param key value notify
     * @param user {@link User}
     * @param uri link for see more info
     */
    @Transactional
    public void createNotify(String key, User user, URI uri){
        Notify notify = this.notifyBuilder.buildNotify(key, user, uri);
        notifyRepository.save(notify);
        userRepository.save(user);
    }

    /**
     * read notification
     * @param notify {@link ru.emiljan.servicedevdevices.models.Notify}
     */
    @Transactional
    public void updateNotify(Notify notify){
        notify.setRead(true);
        notifyRepository.save(notify);
    }

    public List<Notify> findAllByUser(User user){
        return notifyRepository.findAllByUser(user);
    }

    public Notify findById(Long id){
        return this.notifyRepository.findById(id).orElse(null);
    }
}
