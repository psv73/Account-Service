package account.repository;

import account.model.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    Optional<Account> findByEmailIgnoreCase(String username);

    @Modifying
    @Query("UPDATE Account a SET a.failedAttempt = :failedAttempt WHERE LOWER(a.email) = :email")
    void updateFailedAttempts(@Param("failedAttempt") int failAttempts, @Param("email") String email);

}
