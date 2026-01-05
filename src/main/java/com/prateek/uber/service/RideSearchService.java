package com.prateek.uber.service;

import com.prateek.uber.model.Ride;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideSearchService {

    private final MongoTemplate mongoTemplate;

    // helper method to filter rides by user/driver id, with optional status check
    public List<Ride> getRidesByFieldAndStatus(String field, String value, String status) {
        Query query = new Query();

        // if a status is passed, we check for it; otherwise just filter by the field (e.g. userId)
        if (status != null) {
            query.addCriteria(Criteria.where(field).is(value).and("status").is(status));
        } else {
            query.addCriteria(Criteria.where(field).is(value));
        }
        return mongoTemplate.find(query, Ride.class);
    }

    // finds rides where pickup OR drop location matches the keyword (case insensitive)
    public List<Ride> searchRides(String text) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("pickupLocation").regex(text, "i"),
                Criteria.where("dropLocation").regex(text, "i")
        ));
        return mongoTemplate.find(query, Ride.class);
    }

    // finds rides within a specific distance range
    public List<Ride> filterByDistance(Double min, Double max) {
        Query query = new Query();
        query.addCriteria(Criteria.where("distanceKm").gte(min).lte(max));
        return mongoTemplate.find(query, Ride.class);
    }

    // finds rides created between two dates
    public List<Ride> filterByDate(LocalDate start, LocalDate end) {
        // converting LocalDate to Date for Mongo compatibility
        Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Query query = new Query();
        query.addCriteria(Criteria.where("createdAt").gte(startDate).lt(endDate));
        return mongoTemplate.find(query, Ride.class);
    }

    // simple sorting of rides based on fare amount
    public List<Ride> sortByFare(String order) {
        Query query = new Query();
        query.with(Sort.by(order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "fare"));
        return mongoTemplate.find(query, Ride.class);
    }

    // gets all rides for a single specific calendar date
    public List<Ride> getRidesByDate(LocalDate date) {
        Date start = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Query query = new Query();
        query.addCriteria(Criteria.where("createdAt").gte(start).lt(end));
        return mongoTemplate.find(query, Ride.class);
    }

    // complex search: combines status filter, text search, and pagination
    public List<Ride> advancedSearch(String status, String text, int page, int size) {
        Query query = new Query();

        if (status != null && !status.isEmpty()) {
            query.addCriteria(Criteria.where("status").is(status));
        }

        if (text != null && !text.isEmpty()) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("pickupLocation").regex(text, "i"),
                    Criteria.where("dropLocation").regex(text, "i")
            ));
        }

        query.with(PageRequest.of(page, size));
        return mongoTemplate.find(query, Ride.class);
    }
}