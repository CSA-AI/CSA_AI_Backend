package com.nighthawk.spring_portfolio.mvc.person;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"classCodes", "roles", "stats", "performances"})
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<PersonRole> roles = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Map<String, Object>> stats = new HashMap<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<ClassCode> classCodes = new HashSet<>();

    @Column
    private String imageEncoder;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceObject> performances = new ArrayList<>();

    public void addPerformance(PerformanceObject performance) {
        performances.add(performance);
        performance.setPerson(this);
    }

    public Person(String email, String password, String name, Date dob) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.dob = dob;
        this.imageEncoder = null;
    }

    public void addClassCode(ClassCode classCode) {
        if (classCode != null) {
            Set<Person> persons = classCode.getPersons();
            if (persons == null) {
                persons = new HashSet<>();
                classCode.setPersons(persons); // Assuming there's a setter
            }
            persons.add(this);
            classCodes.add(classCode);
        }
    }

    public int getAge() {
        if (this.dob != null) {
            LocalDate birthDay = this.dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthDay, LocalDate.now()).getYears(); 
        }
        return -1;
    }

    public static Person[] init() {
        Person p1 = new Person();
        p1.setName("Thomas Edison");
        p1.setEmail("toby@gmail.com");
        p1.setPassword("123Toby!");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-1840");
            p1.setDob(d);
        } catch (Exception e) {
        }

        Person p2 = new Person();
        p2.setName("Hop");
        p2.setEmail("hop@gmail.com");
        p2.setPassword("123hop");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("04-02-2003");
            p2.setDob(d);
        } catch (Exception e) {
        }

        Person p3 = new Person();
        p3.setName("Admin");
        p3.setEmail("global@csaai.com");
        p3.setPassword("csaAIadmin@8017");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("05-18-1970");
            p3.setDob(d);
        } catch (Exception e) {
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
