package com.nexsol.grape.repository;

import com.nexsol.grape.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findById(Long id);
    Optional<Member> findByPhone(String phone);

    boolean existsByPhone(String phone);
}
