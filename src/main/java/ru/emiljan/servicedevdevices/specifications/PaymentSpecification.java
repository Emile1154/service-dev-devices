package ru.emiljan.servicedevdevices.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.emiljan.servicedevdevices.models.payment.Payment;

import javax.persistence.criteria.Predicate;
import java.util.List;

public class PaymentSpecification {
    public static Specification<Payment> findByKeyword(String keyword, List<String> columns){
        return (root, query, builder)
                -> builder.and(
                root.getModel().getDeclaredSingularAttributes().stream()
                        .filter(column -> columns.contains(column.getName()))
                        .map(column -> builder.like(root.get(column.getName()).as(String.class),
                                "%"+ keyword +"%"))
                        .toArray(Predicate[]::new)
        );
    }
}
