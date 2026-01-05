package com.prateek.uber.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;

    // calculates total earnings, ride count, and average distance for a specific driver
    public Map<String, Object> getDriverSummary(String driverId) {
        Aggregation aggregation = newAggregation(
                // only look at completed rides for this driver
                match(Criteria.where("driverId").is(driverId).and("status").is("COMPLETED")),
                group()
                        .sum("fare").as("totalEarnings")
                        .count().as("totalRides")
                        .avg("distanceKm").as("avgDistance")
        );

        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, "rides", Map.class);
        return result.getUniqueMappedResult();
    }

    // calculates how much a passenger has spent in total
    public Map<String, Object> getUserSpending(String userId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("userId").is(userId).and("status").is("COMPLETED")),
                group()
                        .sum("fare").as("totalSpent")
                        .count().as("totalRides")
        );

        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, "rides", Map.class);
        return result.getUniqueMappedResult();
    }

    // counts how many rides are in each status (requested, accepted, completed)
    public Object getRidesByStatus() {
        Aggregation aggregation = newAggregation(
                group("status").count().as("count")
        );
        return mongoTemplate.aggregate(aggregation, "rides", Map.class).getMappedResults();
    }

    // groups rides by date to show daily volume
    public List<Map> getRidesPerDay() {
        Aggregation aggregation = newAggregation(
                project().and("createdAt").dateAsFormattedString("%Y-%m-%d").as("date"),
                group("date").count().as("totalRides"),
                sort(Sort.Direction.ASC, "_id")
        );
        return mongoTemplate.aggregate(aggregation, "rides", Map.class).getMappedResults();
    }
}