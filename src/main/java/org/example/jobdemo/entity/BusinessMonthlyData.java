package org.example.jobdemo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "business_monthly_data",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"business_id", "reportMonth"})
        }
)
public class BusinessMonthlyData {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(length = 6, nullable = false)
    private String reportMonth;

    private int member;
    private int newMembers;
    private int resignedMembers;
    private BigDecimal billingAmount;

    private String statusCode;
}
