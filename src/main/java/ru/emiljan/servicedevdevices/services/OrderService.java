package ru.emiljan.servicedevdevices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.Order;
import ru.emiljan.servicedevdevices.repositories.OrderRepository;
import ru.emiljan.servicedevdevices.specifications.OrderSpecifications;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author EM1LJAN
 */
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order findById(int id){
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> findAllOrders(){
        return orderRepository.findAll();
    }

    public void deleteOrderById(int id){
        orderRepository.deleteById(id);
    }

    public List<Order> findOrdersByKeyword(List<String> columns, String keyword){
        List<String> columns_bool = columns.stream()
                .filter(s->s.startsWith("is")).collect(Collectors.toList());

        columns.removeIf(columns_bool::contains);

        return orderRepository.findAll(OrderSpecifications.findByKeyword(keyword, columns)
                .and(OrderSpecifications.findTrueBool(columns_bool)));
    }

    public List<String> checkRepeats(Order order){
        List<String> errors = new ArrayList<>();
        if(orderRepository.findOrderByTitle(order.getTitle()) != null){
            errors.add("title");
            errors.add("Данное наименнование проекта уже использовалось");
        }
        if(orderRepository.findOrderByDescription(order.getDescription()) != null){
            errors.add("description");
            errors.add("Данное описание уже было отправлено");
        }
        return errors;
    }

    public void saveOrder(Order order){
        orderRepository.save(order);
    }

    public List<Order> findOrdersByUserId(int id){
        return orderRepository.findOrderByUserId(id);
    }

    @Transactional
    public void update(boolean accepted, boolean completed, int id){
        orderRepository.setOrderInfoById(accepted,completed,id);
    }
}
