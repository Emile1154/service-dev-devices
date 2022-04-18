package ru.emiljan.servicedevdevices.repositories.orderRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.order.CustomOrder;

import java.util.List;

/**
 *
 * @author EM1LJAN
 */
@Repository
public interface OrderRepository extends JpaRepository<CustomOrder, Long> ,
                                         JpaSpecificationExecutor<CustomOrder> {
    List<CustomOrder> findOrderByUserId(Long id);
    CustomOrder findOrderByTitle(String title);

    @Query(value = "SELECT coalesce(max(id),0) FROM orders", nativeQuery = true)
    Long getMaxId();
}
