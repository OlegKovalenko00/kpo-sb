package com.hsebank.repository;


import com.hsebank.domain.BankAccount;


import java.util.Collection;
import java.util.Optional;


public interface BankAccountRepository {
    BankAccount save(BankAccount account);
    Optional<BankAccount> findById(String id);
    Collection<BankAccount> findAll();
    void delete(String id);
}