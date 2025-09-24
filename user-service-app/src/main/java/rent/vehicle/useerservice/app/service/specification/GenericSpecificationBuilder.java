package rent.vehicle.useerservice.app.service.specification;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import rent.vehicle.dto.request.GenericSearchRequest;
import rent.vehicle.dto.request.SearchCriteria;


import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GenericSpecificationBuilder<T> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public Specification buildFromRequest(GenericSearchRequest request) {
        if (request.getSearchCriteriaList() != null && request.getSearchCriteriaList().isEmpty()) {
            return null;
        }

        List<SearchCriteria> searchCriteriaList = request.getSearchCriteriaList();

        List<Specification> specifications = searchCriteriaList.stream()
                .map(this::buildFromCriteria)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return specifications.stream()
                .reduce(Specification::and)
                .orElse(null);
    }

    private Specification<T> buildFromCriteria(SearchCriteria c) {
        return (root, query, cb) -> {
            Path<?> path = resolvePath(root, query, c.getFilter());
            Class<?> javaType = path.getJavaType();
            String stringValue = c.getValue();

            Object value;
            if (javaType.isEnum()) value = convertToEnum(javaType, stringValue);
            else if (javaType == Long.class) value = Long.parseLong(stringValue);
            else if (javaType == Instant.class) value = Instant.parse(stringValue);
            else if (javaType == LocalDate.class) value = LocalDate.parse(stringValue, DATE_FORMATTER);
            else if (javaType == Integer.class) value = Integer.parseInt(stringValue);
            else if (javaType == Double.class) value = Double.parseDouble(stringValue);
            else if (javaType == Boolean.class) value = Boolean.parseBoolean(stringValue);
            else value = stringValue;

            return switch (c.getOperation()) {
                case EQUALS -> cb.equal(path, value);
                case NOT_EQUALS -> cb.notEqual(path, value);
                case CONTAINS -> cb.like(cb.lower(path.as(String.class)), "%" + stringValue.toLowerCase() + "%");
                case STARTS_WITH -> cb.like(cb.lower(path.as(String.class)), stringValue.toLowerCase() + "%");
                case END_WITH -> cb.like(cb.lower(path.as(String.class)), "%" + stringValue.toLowerCase());
                case GREATER_THAN -> {
                    @SuppressWarnings("unchecked") Comparable<Object> v = (Comparable<Object>) value;
                    yield cb.greaterThan(path.as(v.getClass()), v);
                }
                case LESS_THAN -> {
                    @SuppressWarnings("unchecked") Comparable<Object> v = (Comparable<Object>) value;
                    yield cb.lessThan(path.as(v.getClass()), v);
                }
                default -> null;
            };
        };
    }

    private Path<?> resolvePath(From<?, ?> start, CriteriaQuery<?> query, String field) {
        String[] parts = field.split("\\.");
        From<?, ?> from = start;
        Path<?> path = start;
        boolean usedJoin = false;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            boolean isLast = (i == parts.length - 1);

            if (!isLast) {
                // ПРОМЕЖУТОЧНЫЙ сегмент: пытаемся join, иначе fallback на get
                if (from != null) {
                    try {
                        Join<?, ?> j = from.join(part, JoinType.LEFT); // коллекции/ассоциации — ок
                        from = j;
                        path = j;
                        usedJoin = true;
                        continue;
                    } catch (RuntimeException ignored) {
                        // Не joinable (простой атрибут) — идём через get
                        path = from.get(part);
                        if (path instanceof From<?, ?> f) {
                            from = f;
                        } else {
                            from = null; // дальше будем ходить через path.get(...)
                        }
                        continue;
                    }
                } else {
                    path = path.get(part);
                }
            } else {
                // ПОСЛЕДНИЙ сегмент: НИКОГДА не join — только get
                if (from != null) {
                    path = from.get(part);
                } else {
                    path = path.get(part);
                }
            }
        }

        if (usedJoin) {
            query.distinct(true);
        }
        return path;
    }
    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> T convertToEnum(Class<?> enumClass, String value) {
        return Enum.valueOf((Class<T>) enumClass, value);
    }
}
