package com.nighthawk.spring_portfolio.mvc.lstm.stockObj;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stockObject")
public class StockObjectApiController {
    //     @Autowired
    // private JwtTokenUtil jwtGen;
    /*
    #### RESTful API ####
    Resource: https://spring.io/guides/gs/rest-service/
    */

    // Autowired enables Control to connect POJO Object through JPA
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
