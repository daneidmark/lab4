package se.nackademin.java20.lab1.application;

import se.nackademin.java20.lab1.domain.Account;
import se.nackademin.java20.lab1.domain.UserService;
import se.nackademin.java20.lab1.persistance.AccountRepository;
import se.nackademin.java20.lab1.domain.RiskAssesment;

import javax.transaction.Transactional;

public class PersonalFinanceService {
    private final AccountRepository accountRepository;
    private final RiskAssesment riskAssesment;
    private final UserService userService;

    public PersonalFinanceService(AccountRepository accountRepository, RiskAssesment riskAssesment, UserService userService) {
        this.accountRepository = accountRepository;
        this.riskAssesment = riskAssesment;
        this.userService = userService;
    }

    @Transactional
    public Account openAccount(String holder, String password) {
       if(riskAssesment.isPassingCreditCheck(holder)) {
            final Account account = accountRepository.save(new Account(holder, 0));
            userService.enroll(holder, password);
            return account;
        } else {
            throw new RuntimeException("Could not open account due to failing risk assessment");
        }
    }

    @Transactional
    public Account withdraw(long id, String holder, long amount) {
        Account account = accountRepository.findByUserIdAndAccountId(holder, id).orElseThrow(() -> new RuntimeException("Could not find account"));
        account.withdraw(amount);
        accountRepository.save(account);
        return account;
    }

    @Transactional
    public Account deposit(long id, String holder, long amount) {
        Account account = accountRepository.findByUserIdAndAccountId(holder, id).orElseThrow(() -> new RuntimeException("Could not find account"));
        account.deposit(amount);
        accountRepository.save(account);
        return account;
    }

    public Account findAccount(String userId, Long accountId) {
        return accountRepository.findByUserIdAndAccountId(userId, accountId).orElseThrow(() -> new RuntimeException("Could not find account"));
    }

    public Account findByUserName(String username) {
        return accountRepository.findByUserId(username).orElseThrow(() -> new RuntimeException("Could not find Account"));
    }
}
