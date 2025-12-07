package com.hsebank.service;

import com.hsebank.domain.BankAccount;
import com.hsebank.factory.DomainFactory;
import com.hsebank.repository.BankAccountRepository;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

public final class BankAccountService {
    private final BankAccountRepository repo;
    private final DomainFactory factory;

    public BankAccountService(BankAccountRepository repo, DomainFactory factory) {
        this.repo = repo;
        this.factory = factory;
    }
    public BankAccount create(String name) {
        BankAccount acc = factory.createBankAccount(name);
        return repo.save(acc);
    }

    public BankAccount create(String name, BigDecimal balance) {
        BankAccount acc = factory.createBankAccount(name, balance);
        return repo.save(acc);
    }

    public BankAccount createOrUpdateAccount(String id, String name, BigDecimal balance) {
        if (id != null) {
            Optional<BankAccount> existingOpt = repo.findById(id);
            if (existingOpt.isPresent()) {
                BankAccount existing = existingOpt.get();

                boolean updated = false;

                try {
                    Method mName = existing.getClass().getMethod("setName", String.class);
                    mName.invoke(existing, name);
                    updated = true;
                } catch (NoSuchMethodException ignored) {
                } catch (Exception e) {
                    System.out.println("warning: failed to invoke setName: " + e.getMessage());
                }
                try {
                    Method mBal = existing.getClass().getMethod("setBalance", BigDecimal.class);
                    mBal.invoke(existing, balance);
                    updated = true;
                } catch (NoSuchMethodException ignored) {
                } catch (Exception e) {
                    System.out.println("warning: failed to invoke setBalance: " + e.getMessage());
                }

                if (!updated) {
                    BigDecimal current = tryGetBalance(existing);
                    if (current != null && balance != null) {
                        BigDecimal delta = balance.subtract(current);
                        if (delta.compareTo(BigDecimal.ZERO) != 0) {
                            try {
                                Method mApply = existing.getClass().getMethod("applyDelta", BigDecimal.class);
                                mApply.invoke(existing, delta);
                                updated = true;
                            } catch (NoSuchMethodException ignored) {
                            } catch (Exception e) {
                                System.out.println("warning: failed to invoke applyDelta: " + e.getMessage());
                            }
                        }
                    }
                }

                if (updated) {
                    return repo.save(existing);
                }

                // if we couldn't mutate existing instance — replace it
                BankAccount newAcc = factory.createBankAccount(id, name, balance);
                try {
                    repo.delete(id);
                } catch (Exception ignore) { /* ignore */ }
                return repo.save(newAcc);
            } else {
                // not present — create with provided id
                BankAccount acc = factory.createBankAccount(id, name, balance);
                return repo.save(acc);
            }
        } else {
            // no id — create new
            BankAccount acc = factory.createBankAccount(name, balance);
            return repo.save(acc);
        }
    }


    public BankAccount updateAccountName(String id, String newName) {
        BankAccount acc = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("account not found"));

        boolean updated = false;
        try {
            Method m = acc.getClass().getMethod("setName", String.class);
            m.invoke(acc, newName);
            updated = true;
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            System.out.println("warning: failed to invoke setName: " + e.getMessage());
        }

        if (updated) {
            return repo.save(acc);
        } else {
            BigDecimal bal = tryGetBalance(acc);
            BankAccount newAcc = factory.createBankAccount(id, newName, bal);
            repo.delete(id);
            return repo.save(newAcc);
        }
    }


    public void setBalance(String id, BigDecimal balance) {
        BankAccount acc = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("account not found"));

        BigDecimal current = tryGetBalance(acc);
        if (current == null) current = BigDecimal.ZERO;
        BigDecimal delta = balance.subtract(current);

        boolean applied = false;
        try {
            Method mApply = acc.getClass().getMethod("applyDelta", BigDecimal.class);
            mApply.invoke(acc, delta);
            applied = true;
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            System.out.println("warning: failed to invoke applyDelta: " + e.getMessage());
        }

        if (applied) {
            repo.save(acc);
            return;
        }

        try {
            Method mSet = acc.getClass().getMethod("setBalance", BigDecimal.class);
            mSet.invoke(acc, balance);
            repo.save(acc);
            return;
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            System.out.println("warning: failed to invoke setBalance: " + e.getMessage());
        }

        String name = tryGetName(acc);
        BankAccount newAcc = factory.createBankAccount(id, name, balance);
        repo.delete(id);
        repo.save(newAcc);
    }

    public Optional<BankAccount> find(String id) {
        return repo.findById(id);
    }

    public Collection<BankAccount> list() {
        return repo.findAll();
    }

    public void delete(String id) {
        repo.delete(id);
    }


    public void applyDelta(String id, BigDecimal delta) {
        BankAccount acc = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("account not found"));

        try {
            Method mApply = acc.getClass().getMethod("applyDelta", BigDecimal.class);
            mApply.invoke(acc, delta);
            repo.save(acc);
            return;
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            System.out.println("warning: failed to invoke applyDelta: " + e.getMessage());
        }

        BigDecimal current = tryGetBalance(acc);
        if (current == null) current = BigDecimal.ZERO;
        BigDecimal newBal = current.add(delta);
        String name = tryGetName(acc);
        BankAccount newAcc = factory.createBankAccount(id, name, newBal);
        repo.delete(id);
        repo.save(newAcc);
    }

    private BigDecimal tryGetBalance(BankAccount acc) {
        try {
            Method m = acc.getClass().getMethod("getBalance");
            Object v = m.invoke(acc);
            if (v instanceof BigDecimal) return (BigDecimal) v;
        } catch (Exception ignored) { }
        return BigDecimal.ZERO;
    }

    private String tryGetName(BankAccount acc) {
        try {
            Method m = acc.getClass().getMethod("getName");
            Object v = m.invoke(acc);
            if (v instanceof String) return (String) v;
        } catch (Exception ignored) { }
        return null;
    }
}
