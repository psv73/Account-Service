package account.service;

import account.model.Account;
import account.model.enums.ErrorMessageEnum;
import account.model.enums.EventEnum;
import account.model.enums.OperationEnum;
import account.model.enums.RoleEnum;
import account.repository.AccountRepository;
import account.repository.RoleRepository;
import account.request.ChangeRoleRequest;
import account.request.UserAccessRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class AdministrationService {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ChangeRole changeRole;
    private final RoleRepository roleRepository;
    private final SecurityEventService securityEventService;

    public AdministrationService(AccountRepository accountRepository,
                                 AccountService accountService,
                                 ChangeRole changeRole,
                                 RoleRepository roleRepository,
                                 SecurityEventService securityEventService) {

        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.changeRole = changeRole;
        this.roleRepository = roleRepository;
        this.securityEventService = securityEventService;
    }

    public ResponseEntity<?> getAllUsers() {
        return new ResponseEntity<>(
                accountService.getAccounts(accountRepository.findAll()),
                HttpStatus.OK);
    }

    public ResponseEntity<?> deleteUser(String email, String user) {

        Optional<Account> account = accountRepository.findByEmailIgnoreCase(email);

        if (account.isPresent()) {

            if (account.get().getRoles().stream()
                    .anyMatch(role -> role.getRole().matches(RoleEnum.ROLE_ADMINISTRATOR.toString()))) {

                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        ErrorMessageEnum.REMOVE_ADMINISTRATOR.getMessage());

            } else {
                accountRepository.delete(account.get());

                securityEventService.saveEvent(LocalDateTime.now(), EventEnum.DELETE_USER, user, email);

                return new ResponseEntity<>(
                        Map.of("user", email, "status", "Deleted successfully!"),
                        HttpStatus.OK
                );
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    ErrorMessageEnum.USER_NOT_FOUND.getMessage());
        }
    }

    public ResponseEntity<?> changeUserRole(String auth, ChangeRoleRequest changeRoleRequest) {

        String email = changeRoleRequest.getUser();
        String role = changeRoleRequest.getRole();
        String operation = changeRoleRequest.getOperation();

        Optional<Account> account = accountRepository.findByEmailIgnoreCase(email);

        if (account.isEmpty()) {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User " + email + " not found!");
        }

        String roleInDB = "ROLE_" + role;

        if (roleRepository.findByRole(roleInDB).isEmpty()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ErrorMessageEnum.ROLE_NOT_FOUND.getMessage());
        }

        return switch (OperationEnum.valueOf(operation)) {
            case GRANT -> changeRole.grantRole(auth, account.get(), RoleEnum.valueOf(roleInDB));
            case REMOVE -> changeRole.removeRole(auth, account.get(), RoleEnum.valueOf(roleInDB));
        };
    }

    public ResponseEntity<?> changeUserAccess(UserAccessRequest userAccessRequest, Authentication auth) {

        Optional<Account> accountOptional = accountRepository.findByEmailIgnoreCase(userAccessRequest.getUser());

        if (accountOptional.isEmpty()) {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User " + userAccessRequest.getUser() + " not found!");

        } else if (accountOptional.get().getRoles().stream()
                .anyMatch(role -> role.getRole().matches(RoleEnum.ROLE_ADMINISTRATOR.name()))) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ErrorMessageEnum.CAN_NOT_LOCK_ADMINISTRATOR.getMessage());

        } else {
            Account account = accountOptional.get();

            account.setAccountNotLocked(userAccessRequest.getOperation().getValue());

            accountRepository.save(account);

            if (userAccessRequest.getOperation().getValue()) {
                accountService.unLock(account);
            }

            securityEventService.saveEvent(LocalDateTime.now(),
                    userAccessRequest.getOperation().getValue() ?
                            EventEnum.UNLOCK_USER : EventEnum.LOCK_USER,
                    auth.getName(),
                    (userAccessRequest.getOperation().getValue() ?
                    "Unlock user " : "Lock user ") + userAccessRequest.getUser().toLowerCase()
            );

            return new ResponseEntity<>(Map.of("status",
                    "User " + userAccessRequest.getUser().toLowerCase() + " " +
                            userAccessRequest.getOperation().getStatus() + "!"),
                    HttpStatus.OK);
        }
    }
}
