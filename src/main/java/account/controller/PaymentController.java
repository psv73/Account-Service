package account.controller;

import account.constant.AppPath;
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

    @GetMapping(AppPath.PAYMENT)
    public ResponseEntity<?> getPeriodPayment(Authentication auth,
                                              @RequestParam(required = false, name="period") String period) {

        return paymentService.getPayments(accountRepository
                        .findByEmailIgnoreCase(auth.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("Not found")),
                period);
    }

    @PostMapping(AppPath.PAYMENTS)
    public ResponseEntity<?> uploadPayrolls(@RequestBody List<Payment> payments) {
        return paymentService.uploadPayrolls(payments);
    }

    @PutMapping(AppPath.PAYMENTS)
    public ResponseEntity<?> changeSalary(@RequestBody Payment payment) {
        return paymentService.changeSalary(payment);
    }
}
