package ru.emiljan.servicedevdevices.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.emiljan.servicedevdevices.models.User;

import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * @author EM1LJAN
 */
public class UserSpecifications {
    public static Specification<User> findByKeyword(String keyword, List<String> columns){
        return  (root, query, builder)
                -> builder.and(
                root.getModel().getDeclaredSingularAttributes().stream()
                        .filter(column ->
                                columns.contains(column.getName()))
                        .map(column ->
                                builder.like(
                                        root.get(column.getName()).as(String.class),
                                        "%"+keyword+"%"))
                        .toArray(Predicate[]::new)
        );
    }
    public static Specification<User> findTrueBool(List<String> columns) {
        return (root, query, builder)
                -> builder.and(
                root.getModel().getDeclaredSingularAttributes().stream()
                        .filter(column -> columns.contains(column.getName()))
                        .map(column -> builder.equal(root.get(column.getName()), false))
                        .toArray(Predicate[]::new)
        );
    }
}
