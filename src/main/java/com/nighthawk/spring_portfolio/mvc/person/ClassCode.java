package com.nighthawk.spring_portfolio.mvc.person;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.mongodb.lang.NonNull;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import jakarta.persistence.ManyToMany;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.nighthawk.spring_portfolio.mvc.stock.Stock;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"classCodes", "roles", "stats"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ClassCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NonNull
    private String classCode;

    @NonNull
    private String className;

    @NonNull
    private String email;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "classCodes")
    private Set<Person> persons = new HashSet<>();

    public Set<Person> getPersons() {
        return persons;
    }

    @Transactional
    public void setPersons(Set<Person> persons) {
        this.persons = persons;
    }

    @NonNull
    private double totalAccountValue = 100000.00;

    @NonNull
    private double buyingPower = 100000.00;

    // Updated constructor
    public ClassCode(String classCode, String className, String email) {
        this.classCode = classCode;
        this.className = className;
        this.email = email;
        this.totalAccountValue = 100000.00;
        this.buyingPower = 100000.00;
    }

    public ClassCode(String classCode, String className, String email, double totalAccountValue, double buyingPower) {
        this.classCode = classCode;
        this.className = className;
        this.email = email;
        this.totalAccountValue = totalAccountValue;
        this.buyingPower = buyingPower;
    }

    public void setPerson(Person person) {
        this.persons.add(person);
    }

    public static ClassCode[] init() {

        // basics of class construction
        ClassCode c1 = new ClassCode();
        c1.setClassCode("CSA-AI");
        c1.setClassName("CSA-AI");
        c1.setEmail("global@csaai.com");
        c1.setTotalAccountValue(0);
        c1.setBuyingPower(0);

        // Array definition and data initialization
        ClassCode[] codes = {c1};
        return codes;
    }

    public static void main(String[] args) {
        // obtain Person from initializer
        ClassCode[] codes = init();

        // iterate using "enhanced for loop"
        for (ClassCode code : codes) {
            System.out.println(code);  // print object
        }
    }
}
