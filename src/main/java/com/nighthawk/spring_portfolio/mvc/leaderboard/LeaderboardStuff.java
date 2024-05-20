package com.nighthawk.spring_portfolio.mvc.leaderboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LeaderboardStuff {

    @Autowired
    private LeaderboardJpaRepository leaderboardJpaRepository;

@Bean
CommandLineRunner initLeaderboardData() {
    return args -> {
        System.out.println("Initializing leaderboard data...");
        if (leaderboardJpaRepository.count() == 0) {
            leaderboardJpaRepository.save(new Leaderboard("Tay Kim", 100, 2));
            leaderboardJpaRepository.save(new Leaderboard("Ethan Tran", 90, 0));
            leaderboardJpaRepository.save(new Leaderboard("Anthony Bazhenov", 70, 3));
            leaderboardJpaRepository.save(new Leaderboard("Test", 50, 1));
            // Add more static data as needed
            System.out.println("Leaderboard data initialized.");
        }
    };
}

}