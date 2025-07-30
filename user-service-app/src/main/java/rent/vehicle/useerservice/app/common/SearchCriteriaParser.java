package rent.vehicle.useerservice.app.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import rent.vehicle.dto.request.GenericSearchRequest;
import rent.vehicle.dto.request.SearchCriteria;
import rent.vehicle.enums.Operations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchCriteriaParser {

    private List<SearchCriteria> searchCriteriaParse(String filter) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();

        if (filter == null || filter.isBlank()) {
            return searchCriteriaList;
        }

        // Сначала разделяем по запятым на отдельные критерии
        String[] criteriaArray = filter.trim().split(",");

        for (String criteria : criteriaArray) {
            // Затем каждый критерий разделяем по двоеточиям
            String[] parts = criteria.trim().split(":", 3); // 3 - чтобы value могло содержать ":"

            if (parts.length == 3 && !parts[0].isBlank()) {
                try {
                    SearchCriteria searchCriteria = new SearchCriteria();
                    searchCriteria.setFilter(parts[0].trim());
                    searchCriteria.setOperation(Operations.valueOf(parts[1].trim().toUpperCase()));
                    searchCriteria.setValue(parts[2].trim());
                    searchCriteriaList.add(searchCriteria);
                } catch (IllegalArgumentException e) {
                 e.getMessage();
                 throw e;
                }
            }
        }

        return searchCriteriaList;
    }
    public GenericSearchRequest buildSearchRequest(String filter, Pageable pageable) {
        return GenericSearchRequest.builder()
                .searchCriteriaList(searchCriteriaParse(filter))
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .sort(pageable.getSort() != null ? pageable.getSort().toString() : "id,desc")
                .build();
    }

    private String buildSortString(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return "id,desc"; // Дефолтная сортировка
        }

        return sort.stream()
                .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                .collect(Collectors.joining(";"));
    }
}
