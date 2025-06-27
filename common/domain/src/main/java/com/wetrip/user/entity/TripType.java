package com.wetrip.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String travelTypeName;

    // 관계 매핑
//    @OneToMany(mappedBy = "tripType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<UserTripType> userTripTypes;
}