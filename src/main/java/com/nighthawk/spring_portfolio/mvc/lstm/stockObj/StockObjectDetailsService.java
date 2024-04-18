package com.nighthawk.spring_portfolio.mvc.lstm.stockObj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StockObjectDetailsService {
    @Autowired
    private StockObjectJpaRepository stockObjectJpaRepository;

    public void save(StockObject stockObject) {
        stockObjectJpaRepository.save(stockObject);
    }

    public List<StockObject>list(String ticker) {
        return stockObjectJpaRepository.findByTickerContainingIgnoreCase(ticker);
    }
}
