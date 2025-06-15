package rent.vehicle.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;
@Getter
@Setter
public class SearchUserRequest {
    private List<SearchCriteria> searchCriteria;
    private int page = 0;                          // номер страницы (начиная с 0)
    private int size = 20;                         // размер страницы
    private String sort = "id,desc";               // сортировка

}
