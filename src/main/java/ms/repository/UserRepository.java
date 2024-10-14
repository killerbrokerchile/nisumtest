package ms.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ms.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u JOIN FETCH u.phones")
    List<User> findAllWithPhones();

    @Query("SELECT u FROM User u JOIN FETCH u.phones WHERE u.id = :userId")
    User findUserWithPhonesById(@Param("userId") UUID userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

}
