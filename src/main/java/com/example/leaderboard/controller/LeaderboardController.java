package com.example.leaderboard.controller;

import com.example.leaderboard.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @PostMapping("/{username}")
    public ResponseEntity<String> updateScore(
            @PathVariable String username,
            @RequestParam double score) {
        leaderboardService.updateScore(username, score);
        return ResponseEntity.ok(String.format("Updated %s with score %.2f", username, score));
    }

    @GetMapping("/top/{n}")
    public ResponseEntity<Map<String, Double>> getTopN(@PathVariable int n) {
        Map<String, Double> topPlayers = leaderboardService.getTopN(n);
        return ResponseEntity.ok(topPlayers);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getPlayerInfo(@PathVariable String username) {
        Double score = leaderboardService.getScore(username);
        Long rank = leaderboardService.getRank(username);

        if (score == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "username", username,
                "score", score,
                "rank", rank
        ));
    }
}
