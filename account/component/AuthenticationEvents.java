package account.component;

import account.model.Account;
import account.model.enums.EventEnum;
import account.model.enums.RoleEnum;
import account.repository.AccountRepository;
import account.service.AccountService;
import account.service.SecurityEventService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

@Component
public class AuthenticationEvents {

    private final SecurityEventService securityEventService;
    private final AccountRepository repository;
    private final AccountService accountService;

    public AuthenticationEvents(SecurityEventService securityEventService,
                                AccountRepository repository, AccountService accountService) {
        this.securityEventService = securityEventService;
        this.repository = repository;
        this.accountService = accountService;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log(successEvent);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
        log(failureEvent);
    }

    private void log(AbstractAuthenticationEvent event) {

        Optional<Account> accountOptional = repository
                .findByEmailIgnoreCase(event.getAuthentication().getName());

        if (accountOptional.isPresent()) {

            Account account = accountOptional.get();

            if (event instanceof AbstractAuthenticationFailureEvent) {

                if (account.isEnabled() && account.isAccountNotLocked()) {

                    securityEventService.saveEvent(
                            toLocalDateTime(event.getTimestamp()),
                            EventEnum.LOGIN_FAILED,
                            event.getAuthentication().getName());

                    if (account.getFailedAttempt() < AccountService.MAX_FAILED_ATTEMPTS - 1) {
                        accountService.increaseFailedAttempts(account);
                    } else {

                        if (account.getRoles().stream()
                                .noneMatch((r -> r.getRole().equals(RoleEnum.ROLE_ADMINISTRATOR.toString())))
                        ) {
                            accountService.lock(account);

                            securityEventService.saveEvent(
                                    toLocalDateTime(event.getTimestamp()),
                                    EventEnum.BRUTE_FORCE,
                                    event.getAuthentication().getName()
                            );

                            securityEventService.saveEvent(
                                    toLocalDateTime(event.getTimestamp()),
                                    EventEnum.LOCK_USER,
                                    event.getAuthentication().getName(),
                                    "Lock user " + event.getAuthentication().getName()
                            );
                        }
                    }
                }
            } else {
                if (account.getFailedAttempt() > 0) {
                    accountService.resetFailedAttempts(account.getEmail());
                }
            }
        } else {
            securityEventService.saveEvent(
                    toLocalDateTime(event.getTimestamp()),
                    EventEnum.LOGIN_FAILED,
                    event.getAuthentication().getName());
        }
    }

    private LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp / 1000), TimeZone.getDefault().toZoneId()
        );
    }
}
