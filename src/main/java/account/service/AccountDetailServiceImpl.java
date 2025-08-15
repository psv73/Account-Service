package account.service;

import account.model.Account;
import account.repository.AccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class AccountDetailServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    public AccountDetailServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = accountRepository
                .findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return User.withUsername(account.getEmail())
                .password(account.getPassword())
                .authorities(getAuthorities(account))
                .accountExpired(!account.isAccountNotExpired())
                .accountLocked(!account.isAccountNotLocked())
                .disabled(!account.isEnabled())
                .credentialsExpired(!account.isCredentialsNonExpired())
                .build();
    }

    private Collection<GrantedAuthority> getAuthorities(Account account) {

        return account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }
}
