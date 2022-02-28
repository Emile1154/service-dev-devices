package ru.emiljan.servicedevdevices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.Notify;
import ru.emiljan.servicedevdevices.models.User;

import java.util.List;

@Repository
public interface NotifyRepository extends JpaRepository<Notify, Long> {
    List<Notify> findAllByUser(User user);
}
