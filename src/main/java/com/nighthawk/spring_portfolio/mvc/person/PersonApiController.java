package com.nighthawk.spring_portfolio.mvc.person;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
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

    @Autowired
    private PersonJpaRepository repository;

    @Autowired
    private PersonDetailsService personDetailsService;

    @Autowired
    private ClassCodeJpaRepository classCodeRepository;

    public static Set<String> usedClassCodes = new HashSet<>();

    @GetMapping("/")
    public ResponseEntity<List<Person>> getPeople() {
        List<Person> people = repository.findAll();
        people.sort((p1, p2) -> p1.getPerformanceObject().getRating().compareTo(p2.getPerformanceObject().getRating()));
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        return optional.map(person -> new ResponseEntity<>(person, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }


    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<Person> deletePerson(@RequestParam("email") String email) {
        Person person = repository.findByEmail(email);
        if (person != null) {
            repository.deleteByEmail(email);
            return new ResponseEntity<>(person, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/createPerson")
    public ResponseEntity<Object> postPerson(@RequestBody Person personRequest) {
        try {
            Date dob = personRequest.getDob();
            Person person = new Person(personRequest.getEmail(), personRequest.getPassword(), personRequest.getName(), dob, personRequest.getRating());
            personDetailsService.save(person);
            return new ResponseEntity<>(Map.of("message", personRequest.getEmail() + " is created successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error processing the request."), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/createCode")
    public ResponseEntity<Object> createCode(@RequestParam("email") String email) {
        Person person = repository.findByEmail(email);
        if (person == null) {
            return new ResponseEntity<>(Map.of("error", "Person not found"), HttpStatus.NOT_FOUND);
        }
        String classCode = generateUniqueClassCode();
        ClassCode newCode = new ClassCode(classCode);
        person.addClassCode(newCode);
        newCode.setPerson(person);
        classCodeRepository.save(newCode);
        personDetailsService.save(person);
        return new ResponseEntity<>(Map.of("message", email + " New code generated: " + classCode), HttpStatus.OK);
    }

    private String generateUniqueClassCode() {
        int CODE_LENGTH = 6;
        SecureRandom random = new SecureRandom();
        BigInteger randomBigInt;
        do {
            randomBigInt = new BigInteger(50, random);
            String classCode = randomBigInt.toString(32).toUpperCase().substring(0, CODE_LENGTH);
            if (!usedClassCodes.contains(classCode)) {
                usedClassCodes.add(classCode);
                return classCode;
            }
        } while (true);
    }

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> personSearch(@RequestBody final Map<String, String> map) {
        String term = map.get("term");
        List<Person> list = repository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/stockStats")
    public ResponseEntity<Map<String, Integer>> getStockStats() {
        HashMap<String, Integer> data = new HashMap<>();
        // Here you will implement your logic to calculate stock stats based on user data
        // For now, I'll just return an empty map
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping(value = "/updateStocks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> personStats(@RequestBody final Map<String, Object> stat_map) {
        // Here you will implement your logic to update stock stats for a person
        // For now, I'll just return a 200 OK with an empty body
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/stats/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Map<String, Object>>> getStatsById(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        if (optional.isPresent()) {
            Person person = optional.get();
            Map<String, Map<String, Object>> stats = person.getStats();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> putPerson(@RequestParam("email") String email, @RequestBody Person personRequest) {
        Person person = repository.findByEmail(email);
        if (person != null) {
            person.setPassword(personRequest.getPassword());
            person.setName(personRequest.getName());
            person.setDob(personRequest.getDob());
            person.setPerformanceObject(personRequest.getPerformanceObject());
            repository.save(person);
            return new ResponseEntity<>(person, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/createAdmin")
    public ResponseEntity<Object> postAdminPerson(@RequestBody Person personRequest) {
        try {
            Date dob = personRequest.getDob();
            Person person = new Person(personRequest.getEmail(), personRequest.getPassword(), personRequest.getName(), dob, personRequest.getRating());
            personDetailsService.save(person);
            personDetailsService.addRoleToPerson(personRequest.getEmail(), "ROLE_ADMIN");
            return new ResponseEntity<>(Map.of("message", personRequest.getEmail() + " is created successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error processing the request."), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/image/post")
    public ResponseEntity<String> saveImage(MultipartFile image, @RequestParam("username") String username) throws IOException {
        Person existingFileOptional = repository.findByEmail(username);
        if (existingFileOptional != null) {
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] bytearr = image.getBytes();
            String encodedString = encoder.encodeToString(bytearr);
            existingFileOptional.setImageEncoder(encodedString);
            repository.save(existingFileOptional);
            return new ResponseEntity<>("Image saved successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Person not found", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/image/{email}")
    public ResponseEntity<?> downloadImage(@PathVariable String email) {
        Person person = repository.findByEmail(email);
        if (person != null && person.getImageEncoder() != null) {
            byte[] imageBytes = DatatypeConverter.parseBase64Binary(person.getImageEncoder());
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(imageBytes);
        }
        return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
    }
}
