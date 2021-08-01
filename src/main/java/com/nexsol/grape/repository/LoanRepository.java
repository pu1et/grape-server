package com.nexsol.grape.repository;

import com.nexsol.grape.domain.Loan;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class LoanRepository {

    @PersistenceContext
    private EntityManager em;

    public Loan save(Loan loan){
        em.persist(loan);
        return loan;
    }

    public Optional<Loan> findByUserId(Long userId){
        List<Loan> result = em.createQuery("select l from Loan l where l.user_id= :user_id", Loan.class)
                .setParameter("user_id", userId)
                .getResultList();
        return result.stream().findAny();
    }

    public Optional<Loan> findById(Long id){
        Loan loan = em.find(Loan.class, id);
        return Optional.ofNullable(loan);
    }
}
