package com.nighthawk.spring_portfolio.mvc.person;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.arrow.flatbuf.Int;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/api/person")
public class PersonApiController {
    //     @Autowired
    // private JwtTokenUtil jwtGen;
    /*
    #### RESTful API ####
    Resource: https://spring.io/guides/gs/rest-service/
    */

    // Autowired enables Control to connect POJO Object through JPA
    @Autowired
    private PersonJpaRepository repository;

    @Autowired
    private PersonDetailsService personDetailsService;

    /*
    GET List of People
     */
    @GetMapping("/")
    public ResponseEntity<List<Person>> getPeople() {
        return new ResponseEntity<>( repository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    /*
    GET individual Person using ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        if (optional.isPresent()) {  // Good ID
            Person person = optional.get();  // value from findByID
            return new ResponseEntity<>(person, HttpStatus.OK);  // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);       
    }

    /*
    DELETE individual Person using ID
     */
    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<Person> deletePerson(@RequestParam("email") String email) {
        Person person = repository.findByEmail(email);
        
        if (person != null) {  // Check if the person is found
            repository.deleteByEmail(email);
            return new ResponseEntity<>(person, HttpStatus.OK);
        }
        // Person with the given email not found
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /*
    POST Aa record by Requesting Parameters from URI
     */
    @PostMapping( "/createPerson")
    public ResponseEntity<Object> postPerson(@RequestBody Person personRequest) {
        try {
            Date dob = personRequest.getDob();
            Person person = new Person(personRequest.getEmail(), personRequest.getPassword(), personRequest.getName(), dob);
            personDetailsService.save(person);
            return new ResponseEntity<>(Map.of("message", personRequest.getEmail() + " is created successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error processing the request."), HttpStatus.BAD_REQUEST);
        }
    }

    /*
    The personSearch API looks across database for partial match to term (k,v) passed by RequestEntity body
     */
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> personSearch(@RequestBody final Map<String,String> map) {
        // extract term from RequestEntity
        String term = (String) map.get("term");

        // JPA query to filter on term
        List<Person> list = repository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);

        // return resulting list and status, error checking should be added
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/stockStats")
    public ResponseEntity<Map<String, Integer>> getStockStats() {
        List<Person> users = repository.findAll();
        HashMap<String, Integer> data = new HashMap<>();
        
        users.forEach(user -> {
            Map<String, Object> userMap = user.getStats().get("02-15-24");
            if (userMap != null) {
                String[] stocks = {"AAPL", "AMZN", "COST", "GOOGL", "LMT", "META", "MSFT", "NOC", "TSLA", "UNH", "WMT"};
                for (String stock : stocks) {
                    Object value = userMap.get(stock);
                    if (value instanceof Integer) {
                        if (data.containsKey(stock)) {
                            Integer existing = data.get(stock);
                            data.put(stock, existing + (Integer) value);
                        } else {
                            data.put(stock, (Integer) value);
                        }
                    } else if (value instanceof String) {
                        // Handle parsing String to Integer
                        try {
                            Integer intValue = Integer.parseInt((String) value);
                            if (data.containsKey(stock)) {
                                Integer existing = data.get(stock);
                                data.put(stock, existing + intValue);
                            } else {
                                data.put(stock, intValue);
                            }
                        } catch (NumberFormatException e) {
                            // Handle invalid integer format
                            System.err.println("Invalid integer format for stock: " + stock);
                        }
                    } else {
                        // Handle other types if necessary
                        System.err.println("Unsupported type for stock: " + stock);
                    }
                }
            }
        });
        
        return new ResponseEntity<>(data, HttpStatus.OK);
    }    

    @PostMapping(value = "/updateStocks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> personStats(@RequestBody final Map<String,Object> stat_map) {
        // Find ID
        long id;
        Object idObject = stat_map.get("id");
        if (idObject instanceof Integer) {
            id = ((Integer) idObject).longValue();
        } else if (idObject instanceof String) {
            id = Long.parseLong((String) idObject);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Person> optional = repository.findById((id));
        if (optional.isPresent()) {  // Good ID
            Person person = optional.get();  // value from findByID

            // Extract Attributes from JSON
            Map<String, Object> attributeMap = new HashMap<>();
            String[] stocks = {"AAPL", "AMZN", "COST", "GOOGL", "LMT", "META", "MSFT", "NOC", "TSLA", "UNH", "WMT"};

            for (Map.Entry<String,Object> entry : stat_map.entrySet())  {
                // Add all attributes other than "date" and "id" to the "attribute_map"
                if (!entry.getKey().equals("date") && !entry.getKey().equals("id")) {
                    // Handle each stock case
                    for (String stock : stocks) {
                        if (entry.getKey().equals(stock)) {
                            String shares=String.valueOf(entry.getValue());
                            attributeMap.put(entry.getKey(), entry.getValue()); // Add stock attribute
                            break;
                        }
                    }
                    // if (entry.getKey().equals("Balance")) {
                        // // String shares=String.valueOf(entry.getValue());
                        // attributeMap.put(entry.getKey(), entry.getValue()); // Add stock attribute
                        // break;
                    // }
                }
            }

            // Set Date and Attributes to SQL HashMap
            Map<String, Map<String, Object>> date_map = new HashMap<>();
            date_map.put( (String) stat_map.get("date"), attributeMap );
            person.setStats(date_map);  // BUG, needs to be customized to replace if existing or append if new
            repository.save(person);  // conclude by writing the stats updates

            // return Person with update Stats
            return new ResponseEntity<>(person, HttpStatus.OK);
        }
        
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
    }


    @PutMapping("/update")
    public ResponseEntity<Object> putPerson(@RequestParam("email") String email, @RequestBody Person personRequest) {
        try {
            Date dob = personRequest.getDob();  // Assuming getDob() returns a Date
            Person person = repository.findByEmail(email);

            if (person != null) {
                person.setPassword(personRequest.getPassword());
                person.setName(personRequest.getName());
                person.setDob(dob);
                repository.save(person);
                return new ResponseEntity<>(email + " is updated successfully", HttpStatus.OK);
            }

            return new ResponseEntity<>("Person with the given email not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing the request.", HttpStatus.BAD_REQUEST);
        }
    }


    /*
     * POST Aa record by Requesting Parameters from URI
     */
    @PostMapping("/createAdmin")
    // ADI code
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
    //         Person person = new Person(email, password, name, dob);
    //         personDetailsService.save(person);
    //         personDetailsService.addRoleToPerson(email, "ROLE_ADMIN");
    //         return new ResponseEntity<>(email +" is created successfully", HttpStatus.CREATED);
    //     }

    //     return new ResponseEntity<>("Admin key does not match", HttpStatus.BAD_REQUEST);

    // }
    // what is env for
    public ResponseEntity<Object> postAdminPerson(@RequestBody Person personRequest) {
        try {
            Date dob = personRequest.getDob();
            //if (System.getenv("ADMIN_KEY") == adminKey) {
                Person person = new Person(personRequest.getEmail(), personRequest.getPassword(), personRequest.getName(), dob);
                personDetailsService.save(person);
                personDetailsService.addRoleToPerson(personRequest.getEmail(), "ROLE_ADMIN");
                return new ResponseEntity<>(Map.of("message", personRequest.getEmail() + " is created successfully"), HttpStatus.CREATED);
            //}
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error processing the request."), HttpStatus.BAD_REQUEST);
        }
    }


}
