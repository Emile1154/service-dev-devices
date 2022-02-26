package ru.emiljan.servicedevdevices.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.CustomOrder;
import ru.emiljan.servicedevdevices.models.Status;
import ru.emiljan.servicedevdevices.repositories.OrderRepository;
import ru.emiljan.servicedevdevices.services.notify.NotifyService;
import ru.emiljan.servicedevdevices.specifications.OrderSpecifications;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author EM1LJAN
 */
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final NotifyService notifyService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        NotifyService notifyService) {
        this.orderRepository = orderRepository;
        this.notifyService = notifyService;
    }

    public CustomOrder findById(Long id){
        return orderRepository.findById(id).orElse(null);
    }

    public List<CustomOrder> findAllOrders(){
        return orderRepository.findAll();
    }

    @Transactional
    public void deleteOrderById(Long id){
        orderRepository.deleteById(id);
    }

    public List<CustomOrder> findOrdersByKeyword(List<String> columns, String keyword){
        return orderRepository.findAll(OrderSpecifications.findByKeyword(keyword, columns));
    }

    public boolean checkTitle(CustomOrder order){
        if(orderRepository.findOrderByTitle(order.getTitle()) == null){
            return false;
        }
        return true;
    }

    @Transactional
    public void saveOrder(CustomOrder order){
        order.setOrderStatus(Status.NEW);

        notifyService.createNotify("order",order.getUser());
        orderRepository.save(order);
    }

    public List<CustomOrder> findOrdersByUserId(Long id){
        return orderRepository.findOrderByUserId(id);
    }

    @Transactional
    public void update(CustomOrder order, Status status){
        if(order==null){
            return;
        }
        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    public void update(CustomOrder order, BigDecimal price) {
        if(order==null){
            return;
        }
        order.setOrderStatus(Status.ACCEPTED);
        order.setPrice(price);
        orderRepository.save(order);
        notifyService.createNotify("info", order.getUser());
    }
}
