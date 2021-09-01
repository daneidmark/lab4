package se.nackademin.java20.lab1.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.nackademin.java20.lab1.application.PersonalFinanceService;
import se.nackademin.java20.lab1.domain.Account;
import se.nackademin.java20.lab1.security.User;

@Controller
public class AdminResource {

    private final PersonalFinanceService personalFinanceService;

    public AdminResource(PersonalFinanceService personalFinanceService) {
        this.personalFinanceService = personalFinanceService;
    }

    @PostMapping("/admin/open")
    public ResponseEntity<AccountDto> getAccount(@AuthenticationPrincipal User activeUser, @RequestBody OpenAccountRequest openAccountRequest) {
        System.out.println(activeUser.getUsername());
        final Account account = personalFinanceService.openAccount(openAccountRequest.getUserName(), openAccountRequest.getPassword());

        return ResponseEntity.ok(new AccountDto(account.getHolder(), account.getBalance(), account.getId()));
    }

}
