package com.nighthawk.spring_portfolio.mvc.profile;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long>{
    Optional<UploadFile> findByUsername(String username);
}