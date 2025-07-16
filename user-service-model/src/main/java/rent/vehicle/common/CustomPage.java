package rent.vehicle.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomPage<T> {
    private List<T> content;
    private int number;
    private int size;
    private long totalElements;

    @JsonCreator
    public CustomPage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("number") int number,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") long totalElements
    ) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
    }
    public <U> CustomPage<U> map(Function<? super T, ? extends U> mapper) {
        // 2) Преобразуем список content из T в U
        List<U> mappedList = this.content.stream()       // 2.1
                .map(mapper)      // 2.2
                .collect(Collectors.toList()); // 2.3

        // 3) Создаём и возвращаем новый CustomPage<U>
        return new CustomPage<>(                       // 3.1
                mappedList,                                // 3.2
                this.number,                               // 3.3
                this.size,                                 // 3.4
                this.totalElements                         // 3.5
        );
    }
    public static <T> CustomPage<T> from(Page<T> page) {
        return new CustomPage<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    // геттеры и сеттеры
}