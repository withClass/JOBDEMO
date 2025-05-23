package org.example.jobdemo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "business",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"businessName", "registrationNumber"})
        }
)
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String businessName;

    @Column(nullable = false, length = 10)
    private String registrationNumber;

    @Column(length = 10)
    private String postCode;

    @Column(length = 200)
    private String roadAddress;

    @Column(length = 100)
    private String industryName;

    @OneToMany(mappedBy = "business")
    @BatchSize(size = 50)
    private List<BusinessMonthlyData> monthlyDataList;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
