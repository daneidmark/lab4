package se.nackademin.java20.lab1.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.nackademin.java20.lab1.application.PersonalFinanceService;
import se.nackademin.java20.lab1.domain.Account;
import se.nackademin.java20.lab1.security.User;

@Controller
public class AccountResource {

    private final PersonalFinanceService personalFinanceService;

    public AccountResource(PersonalFinanceService personalFinanceService) {
        this.personalFinanceService = personalFinanceService;
    }

    @GetMapping("/account")
    public ResponseEntity<AccountDto> getAccount(@AuthenticationPrincipal User activeUser) {
        System.out.println(activeUser.getUsername());
        final Account account = personalFinanceService.findByUserName(activeUser.getUsername());

        return ResponseEntity.ok(new AccountDto(account.getHolder(), account.getBalance(), account.getId()));
    }

    @PutMapping("/account/deposit")
    public ResponseEntity<AccountDto> deposit(@AuthenticationPrincipal User activeUser, @RequestBody TransactionRequest depositRequest) {
        System.out.println(activeUser.getUsername());
        final Account account = personalFinanceService.deposit(depositRequest.getAccountId(), activeUser.getUsername(), depositRequest.getAmount());

        return ResponseEntity.ok(new AccountDto(account.getHolder(), account.getBalance(), account.getId()));
    }

    @PutMapping("/account/withdraw")
    public ResponseEntity<AccountDto> withdraw(@AuthenticationPrincipal User activeUser, @RequestBody TransactionRequest withdrawRequest) {
        System.out.println(activeUser.getUsername());
        final Account account = personalFinanceService.withdraw(withdrawRequest.getAccountId(), activeUser.getUsername(), withdrawRequest.getAmount());

        return ResponseEntity.ok(new AccountDto(account.getHolder(), account.getBalance(), account.getId()));
    }

}
