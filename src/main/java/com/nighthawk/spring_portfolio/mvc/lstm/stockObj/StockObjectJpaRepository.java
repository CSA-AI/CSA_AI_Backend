package com.nighthawk.spring_portfolio.mvc.lstm.stockObj;

import org.springframework.data.jpa.repository.JpaRepository;

public class StockObjectJpaRepository extends JpaRepository<StockObject, Long>{
    StockObject findByTicker(String ticker);
}
