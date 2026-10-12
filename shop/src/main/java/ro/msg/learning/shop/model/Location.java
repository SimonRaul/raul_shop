package ro.msg.learning.shop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "locations")
public class Location extends BaseEntity{
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Address address;
}
