package com.nighthawk.spring_portfolio.mvc.person;

import static jakarta.persistence.FetchType.EAGER;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nighthawk.spring_portfolio.mvc.performance.PerformanceObject;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"classCodes", "roles", "stats"})
@Convert(attributeName = "person", converter = JsonType.class)
public class Person {

    // automatic unique identifier for Person record
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // email, password, roles are key attributes to login and authentication
    @NotEmpty
    @Size(min=5)
    @Column(unique=true)
    @Email
    private String email;

    @NotEmpty
    private String password;

    @NonNull
    @Size(min = 2, max = 30, message = "Name (2 to 30 chars)")
    private String name;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    @ManyToMany(fetch = EAGER)
    private Collection<PersonRole> roles = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Map<String, Object>> stats = new HashMap<>(); 
    
    @ManyToMany(fetch = EAGER)
    private Set<ClassCode> classCodes = new HashSet<>();

    @Column
	private String ImageEncoder;

    @Column 
    private double rating;

    @OneToOne
    private PerformanceObject performanceObject;

    public void addClassCode(ClassCode classCode) {
        this.classCodes.add(classCode);
        classCode.setPerson(this);
    }
    
    // Constructor used when building object from an API
    public Person(String email, String password, String name, Date dob, double rating) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.dob = dob;
        this.ImageEncoder = null;
        this.rating = rating; 
    }

    // A custom getter to return age from dob attribute
    public int getAge() {
        if (this.dob != null) {
            LocalDate birthDay = this.dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthDay, LocalDate.now()).getYears(); }
        return -1;
    }

    // Initialize static test data 
    public static Person[] init() {

        // basics of class construction
        Person p1 = new Person();
        p1.setName("Thomas Edison");
        p1.setEmail("toby@gmail.com");
        p1.setPassword("123Toby!");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-1840");
            p1.setDob(d);
        } catch (Exception e) {
            // no actions as dob default is good enough
        }

        Person p2 = new Person();
        p2.setName("Hop");
        p2.setEmail("hop@gmail.com");
        p2.setPassword("123hop");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("04-02-2003");
            p2.setDob(d);
        } catch (Exception e) {
            // no actions as dob default is good enough
        }

        Person p3 = new Person();
        p3.setName("Admin");
        p3.setEmail("global@csaai.com");
        p3.setPassword("csaAIadmin@8017");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("05-18-1970");
            p3.setDob(d);
        } catch (Exception e) {
            // no actions as dob default is good enough
        }

        Person[] persons = {p1, p2, p3};
        return persons;
    }

    public static void main(String[] args) {
        Person[] persons = init();

        for (Person person : persons) {
            System.out.println(person);
        }
    }
}
