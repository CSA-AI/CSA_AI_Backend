package com.nighthawk.spring_portfolio.mvc.person;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassCodeJpaRepository extends JpaRepository<ClassCode, Long>{
    ClassCode findByClassCode(String ClassCode);
    ClassCode findByClassName(String ClassName);
    List<ClassCode> findByEmail(String email);

    ClassCode findByClassCodeAndEmail(String ClassCode, String email);

    @Query("SELECT c FROM ClassCode c JOIN c.persons p WHERE p.id = :personId")
    List<ClassCode> findByPersonId(@Param("personId") Long personId);
}
