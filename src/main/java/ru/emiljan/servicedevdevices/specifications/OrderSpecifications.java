package ru.emiljan.servicedevdevices.specifications;
import org.springframework.data.jpa.domain.Specification;
import ru.emiljan.servicedevdevices.models.CustomOrder;

import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * @author EM1LJAN
 */
public class OrderSpecifications {
    public static Specification<CustomOrder> findByKeyword(String keyword, List<String> columns){
        String finalKeyword = "%"+ keyword +"%";
        return (root, query, builder)
                -> builder.and(
                root.getModel().getDeclaredSingularAttributes().stream()
                        .filter(column -> columns.contains(column.getName()))
                        .map(column -> builder.like(root.get(column.getName()).as(String.class),
                                finalKeyword))
                        .toArray(Predicate[]::new)
        );
    }
}
