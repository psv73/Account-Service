package account.repository;

import account.model.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findByEmployeeIgnoreCaseOrderByPeriodDesc(String email);

    Optional<Payment> findByEmployeeIgnoreCaseAndPeriod(String email, String period);
}
