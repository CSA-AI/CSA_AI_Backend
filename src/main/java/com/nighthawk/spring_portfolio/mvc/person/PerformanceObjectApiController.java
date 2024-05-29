package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/accountValue")
    public List<PerformanceObject> getSortedByAccountValue() {
        List<PerformanceObject> performanceObjects = repository.findAll();
        return performanceObjects.stream()
                .sorted((o1, o2) -> Double.compare(o1.getAccountValue(), o2.getAccountValue()))
                .collect(Collectors.toList());
    }

    @PostMapping("/create")
    public ResponseEntity<PerformanceObject> createPerformance(@RequestBody PerformanceObject performanceObject) {
        PerformanceObject savedPerformanceObject = repository.save(performanceObject);
        return new ResponseEntity<>(savedPerformanceObject, HttpStatus.CREATED);
    }
}
