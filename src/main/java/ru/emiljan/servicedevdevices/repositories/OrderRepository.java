package ru.emiljan.servicedevdevices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.CustomOrder;

import java.util.List;

/**
 * @author EM1LJAN
 */
@Repository
public interface OrderRepository extends JpaRepository<CustomOrder, Long> ,
                                         JpaSpecificationExecutor<CustomOrder> {
    List<CustomOrder> findOrderByUserId(Long id);
    CustomOrder findOrderByTitle(String title);
}
