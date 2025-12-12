package hse.kpo.repositories;

import hse.kpo.domains.PaymentDebit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentDebitRepository extends JpaRepository<PaymentDebit, UUID> {
    Optional<PaymentDebit> findByOrderId(String orderId);
    
    boolean existsByOrderId(String orderId);
}
