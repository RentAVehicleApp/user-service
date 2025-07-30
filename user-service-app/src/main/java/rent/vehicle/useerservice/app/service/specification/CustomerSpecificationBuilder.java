package rent.vehicle.useerservice.app.service.specification;

import jakarta.persistence.criteria.From;
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
public class CustomerSpecificationBuilder<T> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public Specification buildFromRequest(GenericSearchRequest request) {
        if(request.getSearchCriteriaList()!=null && request.getSearchCriteriaList().isEmpty()){
            return  null;
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
    private Specification<T> buildFromCriteria(SearchCriteria searchCriteria) {
        return(root, query, criteriaBuilder) -> {
            String field = searchCriteria.getFilter();
            // Вместо простого root.get(field)
            Path<?> path = root;
            String[] fieldParts = field.split("\\.");
            for (String part : fieldParts) {
                if (path instanceof Root || path instanceof Join) {
                    path = ((From<?, ?>) path).get(part);
                } else {
                    path = path.get(part);
                }
            }
            Class<?> javaType = path.getJavaType();

            Object value = null;
            String stringValue = searchCriteria.getValue();

            // Преобразование типов
            if(javaType.isEnum()){
                value = convertToEnum(javaType, stringValue);
            } else if(javaType == Long.class){
                value = Long.parseLong(stringValue);
            } else if(javaType == Instant.class){
                value = Instant.parse(stringValue);
            } else if(javaType == LocalDate.class){
                // Добавлено преобразование для LocalDate
                value = LocalDate.parse(stringValue, DATE_FORMATTER);
            } else if(javaType == Integer.class){
                value = Integer.parseInt(stringValue);
            } else if(javaType == Double.class){
                value = Double.parseDouble(stringValue);
            } else if(javaType == Boolean.class){
                value = Boolean.parseBoolean(stringValue);
            } else {
                value = stringValue;
            }
            return switch (searchCriteria.getOperation()) {
                case EQUALS -> criteriaBuilder.equal(path, value);
                case NOT_EQUALS -> criteriaBuilder.notEqual(path, value);
                case CONTAINS -> criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(field).as(String.class)),
                        "%" + stringValue.toLowerCase() + "%"
                );
                case STARTS_WITH -> criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(field).as(String.class)),
                        stringValue.toLowerCase() + "%"
                );
                case END_WITH -> criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(field).as(String.class)),
                        "%" + stringValue.toLowerCase()
                );
                case GREATER_THAN -> {
                    @SuppressWarnings("unchecked")
                    Comparable<Object> compGt = (Comparable<Object>) value;
                    yield criteriaBuilder.greaterThan(root.get(field).as(compGt.getClass()), compGt);
                }
                case LESS_THAN -> {
                    @SuppressWarnings("unchecked")
                    Comparable<Object> compLt = (Comparable<Object>) value;
                    yield criteriaBuilder.lessThan(root.get(field).as(compLt.getClass()), compLt);
                }
                default -> null;
            };
        };

    }
    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> T convertToEnum(Class<?> enumClass, String value) {
        return Enum.valueOf((Class<T>) enumClass, value);
    }
}
