package com.nighthawk.spring_portfolio.mvc.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.time.LocalDateTime;

/*
Extends the JpaRepository interface from Spring Data JPA.
-- Java Persistent API (JPA) - Hibernate: map, store, update and retrieve database
-- JpaRepository defines standard CRUD methods
-- Via JPA the developer can retrieve database from relational databases to Java objects and vice versa.
 */
public interface StockJpaRepository extends JpaRepository<Stock, Long> {
    Stock findByName(String name);

    List<Stock> findAllByOrderByNameAsc();

    // List<Stock> findByEmailAndTimeBetween(String email, LocalDateTime startTime, LocalDateTime endTime);

    List<Stock> findByEmailOrderByTimeDesc(String email);
    List<Stock> findByEmailAndClassCodeOrderByTimeDesc(String email, String classCode);

    Stock findFirstByNameAndOperationOrderByTimeDesc(String name, String operation);

    List<Stock> findAllByNameAndOperationOrderByTimeDesc(String name, String operation);

    List<Stock> findByClassCodeAndEmailAndTimeBetween(String classCode, String email, LocalDateTime startTime, LocalDateTime endTime);
    List<Stock> findByClassCodeAndEmailAndNameAndOperationAndCostAndTime(String classCode, String email, String name, String operation, Double cost, LocalDateTime time);

    /* Custom JPA query articles, there are articles that show custom SQL as well
       https://springframework.guru/spring-data-jpa-query/
       https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods
    */
    // Custom JPA query
    @Query(
            value = "SELECT * FROM Person p WHERE p.name LIKE ?1 or p.email LIKE ?1",
            nativeQuery = true)
    List<Stock> findByLikeTermNative(String term);
    /*
      https://www.baeldung.com/spring-data-jpa-query
    */
}