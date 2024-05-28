package com.nighthawk.spring_portfolio.mvc.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaderboardJpaRepository extends JpaRepository<Leaderboard, Long> {
    List<Leaderboard> findAllByOrderByScoreDesc();
    List<Leaderboard> findByPlayerNameIgnoreCase(String playerName);
}