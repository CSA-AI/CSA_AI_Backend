package com.nighthawk.spring_portfolio.mvc.stock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/stock")
public class StockApiController {
    //     @Autowired
    // private JwtTokenUtil jwtGen;
    /*
    #### RESTful API ####
    Resource: https://spring.io/guides/gs/rest-service/
    */

    // Autowired enables Control to connect POJO Object through JPA
    @Autowired
    private StockJpaRepository repository;

    @Autowired

    /*
    GET List of People
     */
    @GetMapping("/")
    public ResponseEntity<List<Stock>> getStocks() {
        return new ResponseEntity<>( repository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    /*
    GET individual Person using ID
     */
    @GetMapping("/{name}")
    public ResponseEntity<Stock> getPerson(@PathVariable long name) {
        Optional<Stock> optional = repository.findById(name);
        if (optional.isPresent()) {  // Good ID
            Stock stock = optional.get();  // value from findByID
            return new ResponseEntity<>(stock, HttpStatus.OK);  // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);       
    }

    /*
    DELETE individual Person using ID
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Stock> deletePerson(@PathVariable long id) {
        Optional<Stock> optional = repository.findById(id);
        if (optional.isPresent()) {  // Good ID
            Stock stock = optional.get();  // value from findByID
            repository.deleteById(id);  // value from findByID
            return new ResponseEntity<>(stock, HttpStatus.OK);  // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
    }

    /*
     * POST request to add data for buying or selling a stock
     */
    @PostMapping("/trade")
    public ResponseEntity<Object> buyOrSellStock(@RequestBody Stock tradeRequest) {
        // Extract necessary information from the request body
        String stockName = tradeRequest.getName();
        Double cost = tradeRequest.getCost();
        Integer shares = tradeRequest.getShares();
        String operation = tradeRequest.getOperation();
        LocalDateTime time = tradeRequest.getTime(); // Set the current time
        String email = tradeRequest.getEmail();
        Double totalCost = tradeRequest.calculateTotalCost();
        Double percentChange = null;

        // Perform the operation based on the action
        if (operation.equalsIgnoreCase("buy")) {
            // Create a new stock instance for buying
            Stock stock = new Stock(stockName, email, operation, cost, shares, totalCost, percentChange, time);
            // Save the stock information or process it as needed
            repository.save(stock);
            return new ResponseEntity<>("Stock bought successfully", HttpStatus.OK);
        } else if (operation.equalsIgnoreCase("sell")) {
            // Retrieve the latest buy record
            Stock previousBuy = repository.findFirstByNameAndOperationOrderByTimeDesc(stockName, "buy");
            // Retrieve the latest sell record
            Stock previousSell = repository.findFirstByNameAndOperationOrderByTimeDesc(stockName, "sell");
            // Calculate net shares bought and sold
            int netShares = (previousBuy != null ? previousBuy.getShares() : 0) - (previousSell != null ? previousSell.getShares() : 0);
            // Check if the user owns enough shares to sell
            if (netShares >= shares) {
                // Calculate the percentage change
                Double sellPrice = cost;
                Double buyPrice = previousBuy != null ? previousBuy.getCost() : 0;
                percentChange = previousBuy != null ? previousBuy.calculatePercentChange(sellPrice) : 0;

                // Create a new stock instance for selling
                Stock stock = new Stock(stockName, email, operation, cost, shares, totalCost, percentChange, time);
                // Save the stock information or process it as needed
                repository.save(stock);
                return new ResponseEntity<>("Stock sold successfully. Percentage change: " + percentChange + "%", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not enough shares owned for selling", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Invalid operation", HttpStatus.BAD_REQUEST);
        }
    }

    /*
     * GET trades by email
     */
    @GetMapping("/trades/{email}")
    public ResponseEntity<List<Stock>> getTradesByEmail(@PathVariable String email) {
        List<Stock> trades = repository.findByEmailOrderByTimeDesc(email);
        if (!trades.isEmpty()) {
            return new ResponseEntity<>(trades, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // No trades found for the given email
        }
    }

    /*
     * GET trades by email
     */
    @GetMapping("/tickers/{email}")
    public ResponseEntity<List<String>> getTickersByEmail(@PathVariable String email) {
        List<Stock> trades = repository.findByEmailOrderByTimeDesc(email);
        if (trades.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // No trades found for the given email
        }

        // Map to keep track of the net shares for each ticker
        Map<String, Integer> tickerSharesMap = new HashMap<>();

        // Aggregate shares for each ticker
        for (Stock trade : trades) {
            String ticker = trade.getName();
            int shares = trade.getShares();
            String operation = trade.getOperation();
            int currentShares = tickerSharesMap.getOrDefault(ticker, 0);
            if (operation.equals("buy")) {
                tickerSharesMap.put(ticker, currentShares + shares);
            } else {
                tickerSharesMap.put(ticker, currentShares - shares);
            }
        }

        // List to hold tickers with positive net shares
        List<String> positiveTickers = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tickerSharesMap.entrySet()) {
            if (entry.getValue() > 0) {
                positiveTickers.add(entry.getKey());
            }
        }

        return new ResponseEntity<>(positiveTickers, HttpStatus.OK);
    }

}
