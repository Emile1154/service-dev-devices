package ru.emiljan.servicedevdevices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.Order;
import ru.emiljan.servicedevdevices.models.Status;

import java.util.List;

/**
 * @author EM1LJAN
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> ,
                                         JpaSpecificationExecutor<Order> {
    List<Order> findOrderByUserId(Long id);
    Order findOrderByTitle(String title);
}
