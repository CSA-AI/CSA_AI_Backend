package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ClassCodeService {

    private final ClassCodeJpaRepository classCodeRepository;

    @Autowired
    public ClassCodeService(ClassCodeJpaRepository classCodeRepository) {
        this.classCodeRepository = classCodeRepository;
    }

    public Set<Person> getPersons(Long classCodeId) {
        Optional<ClassCode> classCodeOptional = classCodeRepository.findById(classCodeId);
        if (classCodeOptional.isPresent()) {
            ClassCode classCode = classCodeOptional.get();
            if (classCode.getPersons() == null) {
                classCode.setPersons(new HashSet<>());
            }
            return classCode.getPersons();
        } else {
            throw new RuntimeException("ClassCode not found");
        }
    }

    public  ClassCode list(String classCode) {
        return classCodeRepository.findByClassCode(classCode);
    }

    public ClassCode save(ClassCode classCode) {
        return classCodeRepository.save(classCode);
    }
}
