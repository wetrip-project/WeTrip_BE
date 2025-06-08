package com.wetrip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate travelStartDate; // 여행 시작일

    private LocalDate travelEndDate; // 여행 종료일

    private String destination; // 여행지

    @OneToOne(mappedBy = "schedule", fetch = FetchType.LAZY)
    private User user;
}