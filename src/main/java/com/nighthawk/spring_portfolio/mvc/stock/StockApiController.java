package com.nighthawk.spring_portfolio.mvc.stock;

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
    private StockDetailService stockDetailsService;

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
    POST Aa record by Requesting Parameters from URI <-- NOT NEEDED, all stocks created in backend
     */
    // @PostMapping( "/createPerson")
    // public ResponseEntity<Object> postPerson(@RequestParam("email") String email,
    //                                          @RequestParam("password") String password,
    //                                          @RequestParam("name") String name,
    //                                          @RequestParam("dob") String dobString) {
    //     Date dob;
    //     try {
    //         dob = new SimpleDateFormat("MM-dd-yyyy").parse(dobString);
    //     } catch (Exception e) {
    //         return new ResponseEntity<>(dobString +" error; try MM-dd-yyyy", HttpStatus.BAD_REQUEST);
    //     }
    //     // A stock object WITHOUT ID will create a new record with default roles as student
    //     Stock stock = new Stock(email, password, name, dob);
    //     stockDetailsService.save(stock);
    //     return new ResponseEntity<>(email +" is created successfully", HttpStatus.CREATED);
    // }

    /*
    The stockSearch API looks across database for partial match to term (k,v) passed by RequestEntity body
     */
    // @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<Object> stockSearch(@RequestBody final Map<String,String> map) {
    //     // extract term from RequestEntity
    //     String term = (String) map.get("term");

    //     // JPA query to filter on term
    //     List<Stock> list = repository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);

    //     // return resulting list and status, error checking should be added
    //     return new ResponseEntity<>(list, HttpStatus.OK);
    // }

    /*
    The stockStats API adds stats by Date to Person table 
    */
    @PostMapping(value = "/updateStocks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Stock> stockStats(@RequestBody final Map<String,Object> stat_map) {
    // find ID, added extra error handling bc im slow
        // Extract Attributes from JSON
        String[] stocks = {"AAPL", "AMZN", "COST", "GOOGL", "LMT", "META", "MSFT", "NOC", "TSLA", "UNH", "WMT"};
        for (Map.Entry<String,Object> entry : stat_map.entrySet())  {
            // Add all attributes other than "date" and "id" to the "attribute_map"
            if (!entry.getKey().equals("date") && !entry.getKey().equals("id")) {
                // Handle each stock case
                for (String stk : stocks) {
                    if (entry.getKey().equals(stk)) {
                        Stock stock = repository.findByName(stk);
                        Double cost = Double.valueOf(entry.getValue().toString());
                        stock.setCost(cost);
                        repository.save(stock);  // conclude by writing the stats updates
                        // update the individual repo values
                        break;
                    }
                }
            }
        }
        // Set Date and Attributes to SQL HashMap
        // return Person with update Stats
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // there should be no need for this, we have the previous /updateStocks endpoint for this
    // @PutMapping("/update")
    // public ResponseEntity<Object> putPerson(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("name") String name ) 
    // {
    //     Stock stock = repository.findByEmail(email);
    //     stock.setName(name);
    //     repository.save(stock);
    //     return new ResponseEntity<>(email +" is updated successfully", HttpStatus.OK);
    // }


    /*
     * POST Aa record by Requesting Parameters from URI <-- all stocks should be defined in the backend
     */
    // @PostMapping("/createAdmin")
    // public ResponseEntity<Object> postAdminPerson(@RequestParam("email") String email,
    //                                          @RequestParam("password") String password,
    //                                          @RequestParam("name") String name,
    //                                          @RequestParam("dob") String dobString,
    //                                          @RequestParam("admin_key") String adminKey) {
    //     Date dob;
    //     try {
    //         dob = new SimpleDateFormat("MM-dd-yyyy").parse(dobString);
    //     } catch (Exception e) {
    //         return new ResponseEntity<>(dobString +" error; try MM-dd-yyyy", HttpStatus.BAD_REQUEST);
    //     }

    //     if (System.getenv("ADMIN_KEY") == adminKey) {
    //         Stock stock = new Stock(email, password, name, dob);
    //         stockDetailsService.save(stock);
    //         stockDetailsService.addRoleToPerson(email, "ROLE_ADMIN");
    //         return new ResponseEntity<>(email +" is created successfully", HttpStatus.CREATED);
    //     }

    //     return new ResponseEntity<>("Admin key does not match", HttpStatus.BAD_REQUEST);

    // }


}
