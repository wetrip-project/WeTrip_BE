package user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @Column(nullable = false)
    private Boolean locationEnabled;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;
}