package ru.emiljan.servicedevdevices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.Role;

import java.util.Set;

/**
 *
 * @author EM1LJAN
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(String role);
    @Query(value = "SELECT r FROM Role r WHERE r.role IN :roles")
    Set<Role> findAllByRoles(@Param("roles") Iterable<String> roles);
}
