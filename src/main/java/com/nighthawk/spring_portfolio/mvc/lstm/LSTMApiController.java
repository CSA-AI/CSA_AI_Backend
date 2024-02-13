package com.nighthawk.spring_portfolio.mvc.lstm;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;

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

import com.nighthawk.spring_portfolio.mvc.lstm.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/lstm")
public class LSTMApiController {
    //     @Autowired
    // private JwtTokenUtil jwtGen;
    /*
    #### RESTful API ####
    Resource: https://spring.io/guides/gs/rest-service/
    */


    /*
    GET Predictions for a stock
     */
    @GetMapping("/{ticker}")
    public ResponseEntity<String> getPredictions(@PathVariable String ticker) {
        String imagePath = "src/main/java/com/nighthawk/spring_portfolio/mvc/lstm/resources/graphs/line_chart.png";

        // Path imagePath = Paths.get(directory, ticker);
        ArrayList<String> tickers = new ArrayList<String>(Arrays.asList("GOOGL", "AMZN", "AAPL", "TSLA", "WMT", "MSFT", "META", "COST", "LMT", "NOC", "UNH"));
        if (tickers.contains(ticker)) {
            LSTMMain model = new LSTMMain();
            return new ResponseEntity<>( "HI", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
        
    }

}
