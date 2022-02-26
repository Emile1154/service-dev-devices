package ru.emiljan.servicedevdevices.services.notify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.Notify;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.NotifyRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;

import java.util.List;

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

    @Transactional
    public void createNotify(String key, User user){
        Notify notify = this.notifyBuilder.buildNotify(key, user);
        notifyRepository.save(notify);
        userRepository.save(user);
    }

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
