package se.nackademin.java20.lab1.persistance;

import se.nackademin.java20.lab1.domain.Account;

import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findByUserIdAndAccountId(String holder, long accountId);

    Optional<Account> findByUserId(String holder);
}
