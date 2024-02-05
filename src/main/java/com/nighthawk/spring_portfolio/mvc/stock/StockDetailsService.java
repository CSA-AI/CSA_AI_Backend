package com.nighthawk.spring_portfolio.mvc.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
This class has an instance of Java Persistence API (JPA)
-- @Autowired annotation. Allows Spring to resolve and inject collaborating beans into our bean.
-- Spring Data JPA will generate a proxy instance
-- Below are some CRUD methods that we can use with our database
*/
@Service
@Transactional
public class StockDetailsService implements UserDetailsService {  // "implements" ties ModelRepo to Spring Security
    // Encapsulate many object into a single Bean (Person, Roles, and Scrum)
    @Autowired  // Inject PersonJpaRepository
    private StockJpaRepository personJpaRepository;
    @Autowired  // Inject RoleJpaRepository
    private PersonRoleJpaRepository personRoleJpaRepository;
    // @Autowired  // Inject PasswordEncoder
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /* UserDetailsService Overrides and maps Person & Roles POJO into Spring Security */
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Stock person = personJpaRepository.findByEmail(email); // setting variable user equal to the method finding the username in the database
        if(person==null) {
			throw new UsernameNotFoundException("User not found with username: " + email);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        person.getRoles().forEach(role -> { //loop through roles
            authorities.add(new SimpleGrantedAuthority(role.getName())); //create a SimpleGrantedAuthority by passed in role, adding it all to the authorities list, list of roles gets past in for spring security
        });
        // train spring security to User and Authorities
        return new org.springframework.security.core.userdetails.User(person.getEmail(), person.getPassword(), authorities);
    }

    /* Person Section */

    public  List<Stock>listAll() {
        return personJpaRepository.findAllByOrderByNameAsc();
    }

    // custom query to find match to name or email
    public  List<Stock>list(String name, String email) {
        return personJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(name, email);
    }

    // custom query to find anything containing term in name or email ignoring case
    public  List<Stock>listLike(String term) {
        return personJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
    }

    // custom query to find anything containing term in name or email ignoring case
    public  List<Stock>listLikeNative(String term) {
        String like_term = String.format("%%%s%%",term);  // Like required % rappers
        return personJpaRepository.findByLikeTermNative(like_term);
    }

    // encode password prior to sava
    public void save(Stock person) {
        person.setPassword(passwordEncoder().encode(person.getPassword()));
        personJpaRepository.save(person);
    }

    public Stock get(long id) {
        return (personJpaRepository.findById(id).isPresent())
                ? personJpaRepository.findById(id).get()
                : null;
    }

    public Stock getByEmail(String email) {
        return (personJpaRepository.findByEmail(email));
    }

    public void delete(long id) {
        personJpaRepository.deleteById(id);
    }

    public void defaults(String password) {
        for (Stock person: listAll()) {
            if (person.getPassword() == null || person.getPassword().isEmpty() || person.getPassword().isBlank()) {
                person.setPassword(passwordEncoder().encode(password));
            }
        }
    }


    public void addRoleToPerson(String email, String roleName) { // by passing in the two strings you are giving the user that certain role
        Stock person = personJpaRepository.findByEmail(email);
        if (person != null) {   // verify person
            PersonRole role = personRoleJpaRepository.findByName(roleName);
            if (role != null) { // verify role
                boolean addRole = true;
                for (PersonRole roleObj : person.getRoles()) {    // only add if user is missing role
                    if (roleObj.getName().equals(roleName)) {
                        addRole = false;
                        break;
                    }
                }
                if (addRole) person.getRoles().add(role);   // everything is valid for adding role
            }
        }
    }
    
}