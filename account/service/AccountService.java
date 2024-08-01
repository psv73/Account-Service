package account.service;

import account.dto.AccountDTO;
import account.model.Account;
import account.model.Role;
import account.model.enums.ErrorMessageEnum;
import account.model.enums.EventEnum;
import account.model.enums.RoleEnum;
import account.repository.AccountRepository;
import account.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class AccountService {

    public static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final SecurityEventService securityEventService;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository, ModelMapper modelMapper,
                          SecurityEventService securityEventService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.securityEventService = securityEventService;
    }

    public ResponseEntity<?> signUp(Account account, BindingResult result) {

        if (accountRepository.findByEmailIgnoreCase(account.getEmail()).isPresent()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ErrorMessageEnum.USER_EXIST.getMessage());

        } else if (result.hasErrors()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    result.getFieldErrors().get(0).getDefaultMessage());

        } else if (isPasswordBreached(account.getPassword())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ErrorMessageEnum.PASSWORD_BREACHED.getMessage());
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        Set<Role> roles = new HashSet<>();

        Role role = roleRepository.findByRole(
                        ((Collection<?>) accountRepository.findAll()).isEmpty() ?
                                RoleEnum.ROLE_ADMINISTRATOR.toString() : RoleEnum.ROLE_USER.toString())
                .orElseThrow();

        roles.add(role);

        account.setRoles(roles);

        accountRepository.save(account);

        securityEventService.saveEvent(LocalDateTime.now(), EventEnum.CREATE_USER,
                "Anonymous", account.getEmail());

        return new ResponseEntity<>(this.getAccount(account), HttpStatus.OK);
    }

    public ResponseEntity<?> changePass(BindingResult result, Account account,
                                        String new_password, String email) {

        if (result.hasErrors()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    result.getFieldErrors().get(0).getDefaultMessage());

        } else if (isPasswordBreached(new_password)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ErrorMessageEnum.PASSWORD_BREACHED.getMessage());

        } else if (passwordEncoder.matches(new_password, account.getPassword())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ErrorMessageEnum.PASSWORDS_SAME.getMessage());

        } else {
            account.setPassword(passwordEncoder.encode(new_password));
            accountRepository.save(account);

            securityEventService.saveEvent(LocalDateTime.now(), EventEnum.CHANGE_PASSWORD,
                    email, account.getEmail());

            Map<String, Object> map = new LinkedHashMap<>();

            map.put("email", account.getEmail().toLowerCase());
            map.put("status", "The password has been updated successfully");

            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    private boolean isPasswordBreached(String password) {
        List<String> breachedPass = List.of("PasswordForJanuary", "PasswordForFebruary",
                "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune",
                "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober",
                "PasswordForNovember", "PasswordForDecember");

        return breachedPass.contains(password);
    }

    public AccountDTO getAccount(Account account) {
        return this.modelMapper.map(account, AccountDTO.class);
    }

    public Set<AccountDTO> getAccounts(Iterable<Account> accounts) {

        return StreamSupport.stream(accounts.spliterator(), true)
                .map(this::getAccount)
                .sorted(AccountDTO::compareTo)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void increaseFailedAttempts(Account account) {
        int newFailAttempts = account.getFailedAttempt() + 1;
        accountRepository.updateFailedAttempts(newFailAttempts, account.getEmail());
    }

    public void resetFailedAttempts(String email) {
        accountRepository.updateFailedAttempts(0, email);
    }

    public void lock(Account account) {
        account.setAccountNotLocked(false);
        account.setLockTime(new Date());

        accountRepository.save(account);
    }

    public void unLock(Account account) {
        account.setAccountNotLocked(true);
        resetFailedAttempts(account.getEmail());

        accountRepository.save(account);
    }

    public boolean unlockWhenTimeExpired(Account account) {
        long lockTimeInMillis = account.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            account.setAccountNotLocked(true);
            account.setLockTime(null);
            account.setFailedAttempt(0);

            accountRepository.save(account);

            return true;
        }

        return false;
    }
}