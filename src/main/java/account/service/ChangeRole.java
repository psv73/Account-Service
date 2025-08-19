package account.service;

import account.dto.AccountDTO;
import account.model.Account;
import account.model.Role;
import account.constant.ErrorMessageEnum;
import account.constant.EventEnum;
import account.constant.RoleEnum;
import account.repository.AccountRepository;
import account.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChangeRole {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final SecurityEventService securityEventService;

    public ChangeRole(AccountRepository accountRepository,
                      RoleRepository roleRepository,
                      ModelMapper modelMapper,
                      SecurityEventService securityEventService) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.securityEventService = securityEventService;
    }

    public ResponseEntity<?> removeRole(String auth, Account account, RoleEnum roleEnum) {

        Set<Role> roles = account.getRoles();

        if (roles.stream().noneMatch(r -> r.getRole().equals(roleEnum.toString()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ErrorMessageEnum.USER_DOES_NOT_HAVE_A_ROLE.getMessage()
            );
        }

        Set<Role> clearRoles = roles.stream()
                .filter(r -> !r.getRole().matches(roleEnum.toString()))
                .collect(Collectors.toSet());

        account.setRoles(clearRoles);

        accountRepository.save(account);

        securityEventService.saveEvent(LocalDateTime.now(), EventEnum.REMOVE_ROLE,
                auth, "Remove role " + roleEnum.getRole() + " from " + account.getEmail());

        return new ResponseEntity<>(modelMapper.map(account, AccountDTO.class), HttpStatus.OK);
    }

    public ResponseEntity<?> grantRole(String auth, Account account, RoleEnum roleEnum) {

        Set<Role> roles = account.getRoles();

        if ((!roles.isEmpty() && roleEnum.equals(RoleEnum.ROLE_ADMINISTRATOR)) ||
                roles.stream()
                        .anyMatch(r -> r.getRole().equals(RoleEnum.ROLE_ADMINISTRATOR.toString()))
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ErrorMessageEnum.CAN_NOT_COMBINE_ROLES.getMessage());
        }

        roles.add(roleRepository.findByRole(roleEnum.toString()).orElseThrow());

        account.setRoles(roles);

        accountRepository.save(account);

        securityEventService.saveEvent(LocalDateTime.now(), EventEnum.GRANT_ROLE,
                auth, "Grant role " + roleEnum.getRole() + " to " + account.getEmail());

        return new ResponseEntity<>(
                modelMapper.map(account, AccountDTO.class),
                HttpStatus.OK
        );
    }
}
