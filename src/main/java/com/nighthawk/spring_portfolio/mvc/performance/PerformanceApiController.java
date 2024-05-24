package com.nighthawk.spring_portfolio.mvc.performance;

public class PerformanceApiController {

    @Autowired
    private StockObjectJpaRepository repository;


    /*
    GET List of People
     */
    @GetMapping("/{key}")
    public ResponseEntity<StockObjectIterator> getStocks(@PathVariable String key) {
        StockObjectIterator sIterator = new StockObjectIterator(repository.findAll());
        sIterator.setKeyType(StockObject.KeyType.valueOf(key));
        sIterator.mergeSort(0, sIterator.size()-1);
        return new ResponseEntity<>( sIterator, HttpStatus.OK);
    }
    
}
