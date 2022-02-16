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
public interface UserRepository extends JpaRepository<User, Integer>,
                                        JpaSpecificationExecutor<User> {
    User findByEmail(String email);
    User findByNickname(String nickname);
    User findByPhone(String phoneNumber);

    @Modifying
    @Query(value = "UPDATE users SET account_non_locked = :lock WHERE id = :id",
            nativeQuery = true)
    void setLockById(@Param("id") int id, @Param("lock") boolean lock);

    @Modifying
    @Query(value = "UPDATE users SET active = :value, activation_code = null WHERE id = :id",
        nativeQuery = true)
    void setActiveById(@Param("id") int id, @Param("value") boolean value);

    User findUserByActivateCode(String code);
}
