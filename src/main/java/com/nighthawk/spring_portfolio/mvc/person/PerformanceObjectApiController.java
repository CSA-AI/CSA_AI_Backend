package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/performance")
public class PerformanceObjectApiController {
    @Autowired
    private PerformanceObjectJpaRepository repository;

    @GetMapping("/{key}")
    public ResponseEntity<PerformanceIterator> getPerformances(@PathVariable String key) {
        PerformanceIterator pIterator = new PerformanceIterator(repository.findAll());
        pIterator.setKeyType(PerformanceObject.KeyType.valueOf(key));
        pIterator.mergeSort(0, pIterator.size() - 1);
        return new ResponseEntity<>(pIterator, HttpStatus.OK);
    }
}
