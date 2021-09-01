package se.nackademin.java20.lab1.persistance;

import se.nackademin.java20.lab1.domain.Account;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Optional;

public class AccountRepositoryHibernate implements AccountRepository {
    private final EntityManager em;

    public AccountRepositoryHibernate(EntityManager em) {
        this.em = em;
    }

    public Account save(Account account) {
        em.persist(account);
        return account;
    }

    public Optional<Account> findByUserIdAndAccountId(String holder, long accountId) {
        TypedQuery<Account> q = em.createQuery("SELECT a FROM Account a WHERE a.id = :id and a.holder = :holder", Account.class);
        q.setParameter("id", accountId);
        q.setParameter("holder", holder);
        return Optional.ofNullable(q.getSingleResult());
    }

    @Override
    public Optional<Account> findByUserId(String holder) {
        TypedQuery<Account> q = em.createQuery("SELECT a FROM Account a WHERE a.holder = :holder", Account.class);
        q.setParameter("holder", holder);
        return Optional.ofNullable(q.getSingleResult());
    }
}
