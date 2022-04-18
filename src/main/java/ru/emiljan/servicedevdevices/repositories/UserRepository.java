package ru.emiljan.servicedevdevices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.User;

import java.util.List;

/**
 *
 * @author EM1LJAN
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>,
                                        JpaSpecificationExecutor<User> {
    User findByEmail(String email);
    User findByNickname(String nickname);
    User findByPhone(String phoneNumber);
    User findUserByActivateCode(String code);

    @Modifying
    @Query(value = "UPDATE users SET account_non_locked = :lock WHERE id = :id",
            nativeQuery = true)
    void setLockById(@Param("id") Long id, @Param("lock") boolean lock);

    @Query(value = "SELECT u FROM User u LEFT JOIN u.roles ur WHERE ur.id = :id")
    List<User> findAllByRoles(@Param("id") Long id);

    @Query(value = "SELECT " +
                   "SUM(CASE WHEN un.isRead = FALSE THEN 1 ELSE 0 END ) > 0 " +
                   "FROM User u LEFT JOIN u.notifies un " +
                   "WHERE u = :user")
    boolean checkNotifies(@Param("user") User user);
}
