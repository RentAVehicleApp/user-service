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
    private String unescape(String s) {
        String r = s.replace("\\\\", "\\"); // 1) двойной слэш -> один
        r = r.replace("\\,", ",");          // 2) \, -> ,
        r = r.replace("\\:", ":");          // 3) \: -> :
        return r;
    }

    private List<SearchCriteria> searchCriteriaParse(String filter) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();

        if (filter == null || filter.isBlank()) {
            return searchCriteriaList;
        }

        // Сначала разделяем по запятым на отдельные критерии
        String[] criteriaArray = filter.trim().split("(?<!\\\\),");

        for (String criteria : criteriaArray) {
            // Затем каждый критерий разделяем по двоеточиям
            String[] parts = criteria.trim().split("(?<!\\\\):", 3); // 3 - чтобы value могло содержать ":"
            if (parts.length == 3 && !parts[0].isBlank()) {
                try {
                    String field = unescape(parts[0].trim());
                    String opRaw = unescape(parts[1].trim());
                    String value = unescape(parts[2].trim());

                    SearchCriteria sc = new SearchCriteria();
                    sc.setFilter(field);
                    sc.setOperation(Operations.valueOf(opRaw));
                    sc.setValue(value);

                    searchCriteriaList.add(sc);
                } catch (IllegalArgumentException e) {

                    throw e;
                }
            }
        }

        return searchCriteriaList;
    }
    public GenericSearchRequest buildSearchRequest(String filter, Pageable pageable) {
        String sortString = buildSortString(pageable.getSort());
        return GenericSearchRequest.builder()
                .searchCriteriaList(searchCriteriaParse(filter))
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .sort(sortString)
                .build();
    }

    private String buildSortString(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return "id,desc"; // Дефолтная сортировка
        }

        // Берем только первый элемент сортировки для простоты
        // Если нужна множественная сортировка, придется изменить логику в сервисе
        return sort.stream()
                .findFirst()
                .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                .orElse("id,desc");
    }
}
