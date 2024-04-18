package com.nighthawk.spring_portfolio.mvc.lstm.stockObj;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockObjectJpaRepository extends JpaRepository<StockObject, Long>{
    StockObject findByTicker(String ticker);

    List<StockObject> findByTickerContainingIgnoreCase(String ticker);
}
