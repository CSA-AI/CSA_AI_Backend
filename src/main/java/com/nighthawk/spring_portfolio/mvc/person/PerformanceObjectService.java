package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class PerformanceObjectService {

    @Autowired
    private PerformanceObjectJpaRepository performanceObjectJpaRepository;

   @PostConstruct
    public void init() {
        if (performanceObjectJpaRepository.count() == 0) {
        performanceObjectJpaRepository.save(new PerformanceObject("Alice", 1, 10000.0, 5.0, "A"));
        performanceObjectJpaRepository.save(new PerformanceObject("Bob", 2, 8000.0, 3.0, "B"));
        }
        
    }
    // public static void main(String[] args) {
    //     PerformanceObjectService performanceObjectService = new PerformanceObjectService();
    //     performanceObjectService.init();
    // }
}
