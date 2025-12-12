package hse.kpo.services;

import hse.kpo.domains.Account;
import hse.kpo.dto.requests.TopupRequest;
import hse.kpo.dto.responses.AccountResponse;
import hse.kpo.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(Long userId) {
        if (accountRepository.existsByUserId(userId)) {
            Account existing = accountRepository.findByUserId(userId).get();
            return new AccountResponse(existing.getUserId(), existing.getBalance());
        }
        
        Account account = Account.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();
        
        account = accountRepository.save(account);
        return new AccountResponse(account.getUserId(), account.getBalance());
    }

    @Transactional
    public AccountResponse topup(Long userId, TopupRequest request) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        account.setBalance(account.getBalance().add(request.amount()));
        account = accountRepository.save(account);
        
        return new AccountResponse(account.getUserId(), account.getBalance());
    }

    public Optional<AccountResponse> getBalance(Long userId) {
        return accountRepository.findByUserId(userId)
                .map(account -> new AccountResponse(account.getUserId(), account.getBalance()));
    }
}
