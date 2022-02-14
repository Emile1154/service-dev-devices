package ru.emiljan.servicedevdevices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.Order;

import java.util.List;

/**
 * @author EM1LJAN
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> ,
                                         JpaSpecificationExecutor<Order> {
    List<Order> findOrderByUserId(int id);
    Order findOrderByTitle(String title);
    Order findOrderByDescription(String description);

    @Modifying
    @Query(value = "UPDATE orders SET is_accepted= :accept, is_completed = :complete WHERE id = :id",
            nativeQuery = true)
    void setOrderInfoById(@Param("accept") boolean accept,
                          @Param("complete") boolean complete,
                          @Param("id") int id);

}
