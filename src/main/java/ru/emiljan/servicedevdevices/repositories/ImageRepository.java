package ru.emiljan.servicedevdevices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.Image;

/**
 *
 * @author EM1LJAN
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
