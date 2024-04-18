package com.nighthawk.spring_portfolio.mvc.person;

import com.mongodb.lang.NonNull;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ClassCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NonNull
    String classCode;

    @ManyToOne
    Person person;

    public ClassCode(String ClassCode){
        this.classCode = ClassCode;
    }
}
