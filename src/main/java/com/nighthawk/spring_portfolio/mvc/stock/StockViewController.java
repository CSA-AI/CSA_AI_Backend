package com.nighthawk.spring_portfolio.mvc.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// import jakarta.validation.Valid;
import java.util.List;

// Built using article: https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mvc.html
// or similar: https://asbnotebook.com/2020/04/11/spring-boot-thymeleaf-form-validation-example/
@Controller
@RequestMapping("/mvc/stock")
public class StockViewController {
    // Autowired enables Control to connect HTML and POJO Object to database easily for CRUD
    @Autowired
    private StockDetailService repository;

    @GetMapping("/read")
    public String person(Model model) {
        List<Stock> list = repository.listAll();
        model.addAttribute("list", list);
        return "stock/read";
    }

    /*  The HTML template Forms and PersonForm attributes are bound
        @return - template for person form
        @param - Person Class
    */
    // @GetMapping("/create")
    // public String personAdd(Stock person) {
    //     return "person/create";
    // }

    /* Gathers the attributes filled out in the form, tests for and retrieves validation error
    @param - Person object with @Valid
    @param - BindingResult object
     */
    // @PostMapping("/create")
    // public String personSave(@Valid Stock person, BindingResult bindingResult) {
    //     // Validation of Decorated PersonForm attributes
    //     if (bindingResult.hasErrors()) {
    //         return "person/create";
    //     }
    //     repository.save(person);
    //     repository.addRoleToPerson(person.getEmail(), "ROLE_STUDENT");
    //     // Redirect to next step
    //     return "redirect:/mvc/person/read";
    // }

    // @GetMapping("/update/{id}")
    // public String personUpdate(@PathVariable("id") int id, Model model) {
    //     model.addAttribute("person", repository.get(id));
    //     return "person/update";
    // }

    // @PostMapping("/update")
    // public String personUpdateSave(@Valid Stock person, BindingResult bindingResult) {
    //     // Validation of Decorated PersonForm attributes
    //     if (bindingResult.hasErrors()) {
    //         return "person/update";
    //     }
    //     repository.save(person);
    //     repository.addRoleToPerson(person.getEmail(), "ROLE_STUDENT");

    //     // Redirect to next step
    //     return "redirect:/mvc/person/read";
    // }

    // @GetMapping("/delete/{id}")
    // public String personDelete(@PathVariable("id") long id) {
    //     repository.delete(id);
    //     return "redirect:/mvc/person/read";
    // }

    // @GetMapping("/search")
    // public String person() {
    //     return "person/search";
    // }

}