package com.nighthawk.spring_portfolio.mvc.performance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceObjectJpaRepository extends JpaRepository<PerformanceObject, Long> {
}