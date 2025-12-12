package hse.kpo.repositories;

import hse.kpo.domains.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
    
    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance - :amount, a.version = a.version + 1 WHERE a.userId = :userId AND a.version = :version AND a.balance >= :amount")
    int debitBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("version") Long version);
    
    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance + :amount WHERE a.userId = :userId")
    int creditBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
