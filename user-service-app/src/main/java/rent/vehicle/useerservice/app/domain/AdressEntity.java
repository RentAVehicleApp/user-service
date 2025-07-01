package rent.vehicle.useerservice.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "streetName")
public class AdressEntity {
@Id
@GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
@Column
    private String streetName;
@Column
    private String city;
@Column
    private String apartmentNumber;
@Column
    private String postalCode;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
