package com.nighthawk.spring_portfolio.mvc.image;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface imageRepository extends JpaRepository<image, Long>{
    Optional<image> findByUsername(String username);
}