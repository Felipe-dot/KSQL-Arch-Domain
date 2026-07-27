package com.infrastructure.utils;

import com.infrastructure.order.OrderEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public final class InfraStructureUtils {

    public static <T> Specification<T> like(final String prop, final String term) {
        return (root, query, cb) -> cb.like(cb.upper(root.get(prop)), like(term.toUpperCase()));
    }

    public static Specification<OrderEntity> createAtToday() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Instant startToday = today.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneOffset.UTC)
                .toInstant().minusNanos(1);
        return (root, query, criteria) ->
                criteria.between(root.get("created"), startToday, endOfDay);
    }


    public static String convert(String parameter) {
        LikeSpecification likeSpecification = new LikeSpecification(" LIKE ", parameter);
        return likeSpecification.getProperty() + " '" + like(likeSpecification.getValue()) + "' ";
    }

    private static String like(final String term) {
        return "%" + term + "%";
    }
}
