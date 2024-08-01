package account.repository;

import account.model.Account;
import account.model.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByRole(String role);

    Set<Role> findByAccountList(Account account);
}
