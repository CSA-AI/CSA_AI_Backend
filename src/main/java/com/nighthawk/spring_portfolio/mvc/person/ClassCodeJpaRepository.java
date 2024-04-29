package com.nighthawk.spring_portfolio.mvc.person;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassCodeJpaRepository extends JpaRepository<ClassCode, Long>{
    ClassCode findByClassCode(String ClassCode);
    List<ClassCode> findByPersonId(Long PersonId);
}
