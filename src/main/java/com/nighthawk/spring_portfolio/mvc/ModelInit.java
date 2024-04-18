package com.nighthawk.spring_portfolio.mvc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;
import com.nighthawk.spring_portfolio.mvc.person.PersonRole;
import com.nighthawk.spring_portfolio.mvc.person.PersonRoleJpaRepository;
import com.nighthawk.spring_portfolio.mvc.lstm.stockObj.StockObject;
import com.nighthawk.spring_portfolio.mvc.lstm.stockObj.StockObjectDetailsService;
import com.nighthawk.spring_portfolio.mvc.lstm.stockObj.StockObjectJpaRepository;

import java.util.List;
import java.util.ArrayList;

@Component
@Configuration // Scans Application for ModelInit Bean, this detects CommandLineRunner
public class ModelInit {  
    @Autowired PersonDetailsService personService;
    @Autowired StockObjectDetailsService stockObjectService;
    @Autowired PersonRoleJpaRepository roleRepo;

    @Bean
    CommandLineRunner run() {  // The run() method will be executed after the application starts
        return args -> {

            PersonRole[] personRoles = PersonRole.init();
            for (PersonRole role : personRoles) {
                PersonRole existingRole = roleRepo.findByName(role.getName());
                if (existingRole != null) {
                    // role already exists
                    continue;
                } else {
                    // role doesn't exist
                    roleRepo.save(role);
                }
            }

            // Person database is populated with test data
            Person[] personArray = Person.init();
            
            for (Person person : personArray) {
                //findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase
                List<Person> personFound = personService.list(person.getName(), person.getEmail());  // lookup
                if (personFound.size() == 0) {
                    personService.save(person);  // save
                    personService.addRoleToPerson(person.getEmail(), "ROLE_STUDENT");
                    
                }
            }
            for (int i = 1; i <= 2 && i < personArray.length; i++) {
                personService.addRoleToPerson(personArray[i].getEmail(), "ROLE_ADMIN");
            }

            ArrayList<StockObject> stockObjectArray = StockObject.init();
            for (StockObject stock : stockObjectArray) { 
                List<StockObject> stockFound = stockObjectService.list(stock.getTicker());
                if (stockFound.size() == 0) {
                    stockObjectService.save(stock);
                }
            }
        };
    }
}