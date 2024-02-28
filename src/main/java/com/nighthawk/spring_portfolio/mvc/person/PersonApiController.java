package com.nighthawk.spring_portfolio.mvc.person;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

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
import org.springframework.web.multipart.MultipartFile;



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

    @Autowired
    private ClassCodeJpaRepository classCodeRepository;

    public static Set<String> usedClassCodes = new HashSet<>();
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

    // FIX BECAUSE NOT USING JSON
    // @PostMapping( "/post")
    // public ResponseEntity<Object> postPerson(@RequestParam("email") String email,
    //                                          @RequestParam("password") String password,
    //                                          @RequestParam("name") String name,
    //                                          @RequestParam("dob") String dobString, 
    //                                          @RequestParam("role") String role) {
    //     Date dob;
    //     System.out.println("\t\t\t\t\t"+email+"\t\t\t\t\t");
    //     try {
    //         dob = new SimpleDateFormat("MM-dd-yyyy").parse(dobString);
    //     } catch (Exception e) {
    //         return new ResponseEntity<>(Map.of("error", "Error processing the request."), HttpStatus.BAD_REQUEST);
    //     }

    //     List<Person> humans = repository.findAll();
    //     List<ClassCode> dataCodes = classCodeRepository.findAll();
    //     if (dataCodes != null){
    //         for ( ClassCode dataCode : dataCodes){
    //             usedClassCodes.add(dataCode.getClassCode());
    //         }
    //     }

    //     // A person object WITHOUT ID will create a new record with default roles as student
    //     Person person = new Person(email, password, name, dob);
    //     personDetailsService.save(person);

    //     personDetailsService.addRoleToPerson(email, role);
    //     String classCode = ""; 
    //     if ("ROLE_TEACHER".equals(role)){
    //         System.out.println("Creating code");
    //         int CODE_LENGTH = 6; 
    //         SecureRandom random = new SecureRandom();
    //         BigInteger randomBigInt;
    //         do {
    //             randomBigInt = new BigInteger(50, random);
    //             classCode = randomBigInt.toString(32).toUpperCase().substring(0, CODE_LENGTH);
    //         } while (usedClassCodes.contains(classCode));
    //         usedClassCodes.add(classCode);
    //         System.out.println(classCode);
    //     }

    //     // ArrayList<String> classcodes = new ArrayList<String>();
    //     // classcodes.add(classCode);
    //     // System.out.println(classCode);
    //     // person.setClassCodes(classcodes);
    //     ClassCode adding = new ClassCode(classCode);
    //     person.addClassCode(adding);
    //     adding.setPerson(person);

    //     classCodeRepository.save(adding);
    //     personDetailsService.save(person);
    //     String test;
    //     if(classCode.isBlank()){
    //         test = "Not work";
    //     }
    //     else{
    //         test = "works";
    //     }
    //     return new ResponseEntity<>(name +" is created successfully" + test, HttpStatus.CREATED);
    // }

    @PostMapping("/createCode")
    public ResponseEntity<Object> createCode(@RequestParam("email") String email){
        
        Person person = repository.findByEmail(email);
        String classCode = "";
        System.out.println("Creating code");
        int CODE_LENGTH = 6; 
        SecureRandom random = new SecureRandom();
        BigInteger randomBigInt;
        do {
            randomBigInt = new BigInteger(50, random);
            classCode = randomBigInt.toString(32).toUpperCase().substring(0, CODE_LENGTH);
        } while (usedClassCodes.contains(classCode));
        usedClassCodes.add(classCode);
        System.out.println(classCode);

        ClassCode newCode = new ClassCode(classCode);
        person.addClassCode(newCode);
        newCode.setPerson(person);
        classCodeRepository.save(newCode);
        personDetailsService.save(person);
    
        return new ResponseEntity<>(email + "New code generated", HttpStatus.OK);
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

        // Retrieve person from repository
        Optional<Person> optional = repository.findById(id);
        if (optional.isPresent()) {
            Person person = optional.get();

            // Extract attributes from JSON payload
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("ticker", stat_map.get("ticker"));
            attributes.put("shares", stat_map.get("shares"));
            attributes.put("price", stat_map.get("price"));

            // Get current date
            String currentDate = (String) stat_map.get("date");

            // Update or append to existing stats map
            Map<String, Map<String, Object>> stats = person.getStats();
            if (stats.containsKey(currentDate)) {
                // Update existing entry
                stats.get(currentDate).putAll(attributes);
            } else {
                // Append new entry
                stats.put(currentDate, attributes);
            }

            // Set updated stats and save person
            person.setStats(stats);
            repository.save(person);

            return new ResponseEntity<>(person, HttpStatus.OK);
        }

        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
    }   

    @GetMapping(value = "/stats/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Map<String, Object>>> getStatsById(@PathVariable long id) {
        // Retrieve person from repository
        Optional<Person> optional = repository.findById(id);
        if (optional.isPresent()) {
            Person person = optional.get();
            // Get stats data from the person object
            Map<String, Map<String, Object>> stats = person.getStats();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        }
        // Person not found
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

    @PostMapping("/image/post")
    public ResponseEntity<String> saveImage(MultipartFile image, @RequestParam("username") String username) throws IOException {
        Person existingFileOptional = repository.findByEmail(username);
        System.out.println(username);
        if (existingFileOptional != null) {
            // Person existingFile = existingFileOptional.get();
            System.out.println("Person exists");
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] bytearr = image.getBytes();
            String encodedString = encoder.encodeToString(bytearr);

            existingFileOptional.setImageEncoder(encodedString);
            repository.save(existingFileOptional);
            System.out.println("Image added");

            return new ResponseEntity<>("It is created successfully", HttpStatus.CREATED);
        } else {
            // Base64.Encoder encoder = Base64.getEncoder();
            // byte[] bytearr = image.getBytes();
            // String encodedString = encoder.encodeToString(bytearr);
            // Person file = new Person(username, encodedString);
            // repository.save(file);
            System.out.println("Person does not exist");
            return new ResponseEntity<>("It is created successfully", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/image/{email}")
    public ResponseEntity<?> downloadImage(@PathVariable String email) {
        Person optional = repository.findByEmail(email);
        // Person file = optional.get();
        String data = optional.getImageEncoder();
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(data);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageBytes);
    }
    
}
