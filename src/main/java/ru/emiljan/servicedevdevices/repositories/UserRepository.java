package ru.emiljan.servicedevdevices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.emiljan.servicedevdevices.models.User;

/**
 * @author EM1LJAN
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>,
                                        JpaSpecificationExecutor<User> {
    User findByEmail(String email);
    User findByNickname(String nickname);
    User findByPhone(String phoneNumber);

    @Modifying
    @Query(value = "UPDATE users SET account_non_locked = :lock WHERE id = :id",
            nativeQuery = true)
    void setLockById(@Param("id") Long id, @Param("lock") boolean lock);

    @Modifying
    @Query(value = "UPDATE users SET active = true, activation_code = null WHERE id = :id",
        nativeQuery = true)
    void setActiveById(@Param("id") Long id);

    User findUserByActivateCode(String code);
}
