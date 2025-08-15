package account.controller;

import account.model.Payment;
import account.repository.AccountRepository;
import account.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final AccountRepository accountRepository;

    public PaymentController(PaymentService paymentService, AccountRepository accountRepository) {
        this.paymentService = paymentService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/api/empl/payment")
    public ResponseEntity<?> getPeriodPayment(Authentication auth,
                                              @RequestParam(required = false) String period) {

        return paymentService.getPayments(accountRepository
                        .findByEmailIgnoreCase(auth.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("Not found")),
                period);
    }

    @PostMapping("/api/acct/payments")
    public ResponseEntity<?> uploadPayrolls(@RequestBody List<Payment> payments) {
        return paymentService.uploadPayrolls(payments);
    }

    @PutMapping("/api/acct/payments")
    public ResponseEntity<?> changeSalary(@RequestBody Payment payment) {
        return paymentService.changeSalary(payment);
    }
}
