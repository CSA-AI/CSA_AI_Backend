package com.nighthawk.spring_portfolio.mvc.stock;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.StockDetailService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import java.util.ArrayList;
// import java.util.Collection;
import java.util.List;

/*
This class has an instance of Java Persistence API (JPA)
-- @Autowired annotation. Allows Spring to resolve and inject collaborating beans into our bean.
-- Spring Data JPA will generate a proxy instance
-- Below are some CRUD methods that we can use with our database
*/
@Service
@Transactional
public class StockDetailService {  // "implements" ties ModelRepo to Spring Security
    // Encapsulate many object into a single Bean (stock, Roles, and Scrum)
    @Autowired  // Inject stockJpaRepository
    private StockJpaRepository stockJpaRepository;

    /* stock Section */

    public  List<Stock>listAll() {
        return stockJpaRepository.findAllByOrderByNameAsc();
    }

    // custom query to find anything containing term in name or email ignoring case
    public  List<Stock>listLikeNative(String term) {
        String like_term = String.format("%%%s%%",term);  // Like required % rappers
        return stockJpaRepository.findByLikeTermNative(like_term);
    }

    public Stock get(long id) {
        return (stockJpaRepository.findById(id).isPresent())
                ? stockJpaRepository.findById(id).get()
                : null;
    }

    public Stock getByName(String name) {
        return (stockJpaRepository.findByName(name));
    }

    public void delete(long id) {
        stockJpaRepository.deleteById(id);
    }
}