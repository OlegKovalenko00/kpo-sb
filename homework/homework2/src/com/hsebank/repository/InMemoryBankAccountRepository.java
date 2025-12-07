package com.hsebank.repository;


import com.hsebank.domain.BankAccount;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public final class InMemoryBankAccountRepository implements BankAccountRepository {
    private final Map<String, BankAccount> map = new ConcurrentHashMap<>();


    public BankAccount save(BankAccount account) {
        map.put(account.getId(), account);
        return account;
    }


    public Optional<BankAccount> findById(String id) { return Optional.ofNullable(map.get(id)); }
    public Collection<BankAccount> findAll() { return Collections.unmodifiableCollection(map.values()); }
    public void delete(String id) { map.remove(id); }
}