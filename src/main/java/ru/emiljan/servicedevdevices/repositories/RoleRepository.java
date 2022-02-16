package ru.emiljan.servicedevdevices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.Role;

/**
 * @author EM1LJAN
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String role);
}
