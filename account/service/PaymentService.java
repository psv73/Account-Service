package account.service;

import account.dto.AccountDTO;
import account.model.Account;
import account.model.enums.ErrorMessageEnum;
import account.model.Payment;
import account.repository.AccountRepository;
import account.repository.PaymentRepository;
import account.response.PaymentResponse;
import account.response.ResponseMessage;
import account.utils.PaymentUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PlatformTransactionManager trManager;
    private final ResponseMessage responseMessage;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public PaymentService(PaymentRepository paymentRepository, PlatformTransactionManager trManager,
                          ResponseMessage responseMessage, AccountRepository accountRepository, ModelMapper modelMapper) {
        this.paymentRepository = paymentRepository;
        this.trManager = trManager;
        this.responseMessage = responseMessage;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<?> getAllPayments(Account account) {

        List<Payment> payments = paymentRepository.findByEmployeeIgnoreCaseOrderByPeriodDesc(account.getEmail());

        if (!payments.isEmpty()) {

            List<PaymentResponse> response = new ArrayList<>();

            for (Payment payment : payments) {
                PaymentResponse resp = new PaymentResponse(
                        account.getName(),
                        account.getLastName(),
                        PaymentUtil.convertPeriod(payment.getPeriod()),
                        PaymentUtil.convertSalary(payment.getSalary())
                );
                response.add(resp);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(modelMapper.map(account, AccountDTO.class), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> getPayments(Account account, String period) {

        if (period == null) {
            return getAllPayments(account);
        } else {
            return getPeriodPayment(account, period);
        }
    }

    public ResponseEntity<?> getPeriodPayment(Account account, String period) {

        Optional<Payment> payment =
                paymentRepository.findByEmployeeIgnoreCaseAndPeriod(account.getEmail(), period);

        if (payment.isPresent()) {
            return new ResponseEntity<>(new PaymentResponse(account.getName(),
                    account.getLastName(),
                    PaymentUtil.convertPeriod(payment.get().getPeriod()),
                    PaymentUtil.convertSalary(payment.get().getSalary())
            ), HttpStatus.OK);
        } else {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ErrorMessageEnum.ERROR.getMessage()
            );
        }
    }

    public ResponseEntity<?> uploadPayrolls(List<Payment> payments) {

        TransactionDefinition trDefinition = new DefaultTransactionDefinition();
        TransactionStatus trStatus = trManager.getTransaction(trDefinition);
        ResponseEntity<?> response;

        try {

            if (payments.stream().distinct().toList().size() != payments.size()) {
                throw new RuntimeException(ErrorMessageEnum.ERROR.getMessage());
            }

            StringBuilder errorMessage = new StringBuilder();

            for (int i = 0; i < payments.size(); i++) {
                if (accountRepository.findByEmailIgnoreCase(payments.get(i).getEmployee()).isPresent()) {

                    String error = checkPayments(payments.get(i), i);

                    if (!error.isEmpty()) {
                        errorMessage
                                .append(errorMessage.isEmpty() ? "" : ", ")
                                .append(error);
                    }

                    paymentRepository.save(payments.get(i));

                } else {
                    throw new RuntimeException(ErrorMessageEnum.USER_NOT_FOUND.getMessage());
                }
            }

            if (!errorMessage.isEmpty()) {
                throw new RuntimeException(errorMessage.toString());
            } else {
                trManager.commit(trStatus);

                response = new ResponseEntity<>(
                        Map.of("status", "Added successfully!"),
                        HttpStatus.OK
                );
            }
        } catch (Exception ex) {
            response = responseMessage.getResponseBadRequest(ex.getMessage(),
                    "/api/acct/payments");
            trManager.rollback(trStatus);
        }

        return response;
    }

    public ResponseEntity<?> changeSalary(Payment newPayment) {

        TransactionDefinition trDefinition = new DefaultTransactionDefinition();
        TransactionStatus trStatus = trManager.getTransaction(trDefinition);
        ResponseEntity<?> response;

        try {
            Optional<Payment> paymentOpt = paymentRepository.findByEmployeeIgnoreCaseAndPeriod(
                    newPayment.getEmployee(), newPayment.getPeriod());

            if (paymentOpt.isEmpty()) {
                throw new RuntimeException(ErrorMessageEnum.PAYMENT_NOT_FOUND.getMessage());
            }

            Payment payment = paymentOpt.get();

            payment.setSalary(newPayment.getSalary());

            paymentRepository.save(payment);

            trManager.commit(trStatus);

            response = new ResponseEntity<>(
                    Map.of("status", "Updated successfully!"),
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            response = responseMessage.getResponseBadRequest(ex.getMessage(),
                    "/api/acct/payments");
            trManager.rollback(trStatus);
        }

        return response;
    }

    private String checkPayments(Payment payment, int index) {
        StringBuilder errorMessage = new StringBuilder();

        if (!payment.getPeriod().matches("(0[1-9]|1[0-2])-2[0-9]{3}")) {
            errorMessage
                    .append("payments[")
                    .append(index)
                    .append("].period: ")
                    .append(ErrorMessageEnum.WRONG_DATE);
        }

        if (payment.getSalary() < 0) {
            errorMessage
                    .append("payments[")
                    .append(index)
                    .append("].salary: ")
                    .append(ErrorMessageEnum.NEGATIVE_SALARY);
        }

        return errorMessage.toString();
    }
}
