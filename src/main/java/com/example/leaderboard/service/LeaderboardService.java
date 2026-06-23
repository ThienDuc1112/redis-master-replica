package com.example.leaderboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class LeaderboardService {

    private static final String LEADERBOARD_KEY = "game:leaderboard";

    @Autowired
    @Qualifier("masterRedisTemplate")
    private RedisTemplate<String, Object> masterRedisTemplate;

    @Autowired
    @Qualifier("replicaRedisTemplate")
    private RedisTemplate<String, Object> replicaRedisTemplate;

    /**
     * Cập nhật điểm cho user (ghi vào Master)
     */
    public void updateScore(String username, double score) {
        masterRedisTemplate.opsForZSet().add(LEADERBOARD_KEY, username, score);
    }

    /**
     * Lấy top N game thủ (đọc từ Replica)
     */
    public Map<String, Double> getTopN(int n) {
        ZSetOperations<String, Object> zSetOps = replicaRedisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> topN = zSetOps.reverseRangeWithScores(LEADERBOARD_KEY, 0, n - 1);

        Map<String, Double> result = new LinkedHashMap<>();
        if (topN != null) {
            for (ZSetOperations.TypedTuple<Object> tuple : topN) {
                result.put(tuple.getValue().toString(), tuple.getScore());
            }
        }
        return result;
    }

    /**
     * Lấy điểm của một user cụ thể
     */
    public Double getScore(String username) {
        return replicaRedisTemplate.opsForZSet().score(LEADERBOARD_KEY, username);
    }

    /**
     * Lấy rank của user (0-based)
     */
    public Long getRank(String username) {
        Long rank = replicaRedisTemplate.opsForZSet().reverseRank(LEADERBOARD_KEY, username);
        return rank != null ? rank + 1 : null;
    }
}
