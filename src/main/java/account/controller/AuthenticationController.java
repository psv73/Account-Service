package account.controller;

import account.model.Account;
import account.repository.AccountRepository;
import account.request.NewPasswordRequest;
import account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public AuthenticationController(AccountService accountService, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @PostMapping("/api/auth/signup")
    public ResponseEntity<?> singUp(@Valid @RequestBody Account account, BindingResult result) {
        return accountService.signUp(account, result);
    }

    @PostMapping("/api/auth/changepass")
    public ResponseEntity<?> changepass(Authentication auth,
                                        @Valid @RequestBody NewPasswordRequest newPasswordRequest,
                                        BindingResult result) {

        return accountService.changePass(result,
                accountRepository.findByEmailIgnoreCase(auth.getName()).orElseThrow(() ->
                        new UsernameNotFoundException("Not found")),
                newPasswordRequest.getPassword(), auth.getName());
    }
}
