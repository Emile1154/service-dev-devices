package ru.emiljan.servicedevdevices.repositories.orderRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emiljan.servicedevdevices.models.order.FileInfo;

public interface FileRepository extends JpaRepository<FileInfo, Long> {
}
