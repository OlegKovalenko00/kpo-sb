package hse.kpo.controllers;

import hse.kpo.dto.requests.TopupRequest;
import hse.kpo.dto.responses.AccountResponse;
import hse.kpo.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "API for payment management")
public class PaymentController {

    private final AccountService accountService;

    @PostMapping("/account")
    @Operation(summary = "Create account for user")
    public ResponseEntity<AccountResponse> createAccount(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(accountService.createAccount(userId));
    }

    @PostMapping("/account/topup")
    @Operation(summary = "Top up account balance")
    public ResponseEntity<AccountResponse> topup(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody TopupRequest request) {
        return ResponseEntity.ok(accountService.topup(userId, request));
    }

    @GetMapping("/account/balance")
    @Operation(summary = "Get account balance")
    public ResponseEntity<AccountResponse> getBalance(@RequestHeader("X-User-Id") Long userId) {
        return accountService.getBalance(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
