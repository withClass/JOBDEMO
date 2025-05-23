package org.example.jobdemo.repository;

import lombok.RequiredArgsConstructor;
import org.example.jobdemo.dto.BusinessMonthlyDataDto;
import org.example.jobdemo.dto.OpenApiBusinessDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BusinessJdbcRepository {
    private final DataSource dataSource;

    public void batchInsert(List<OpenApiBusinessDto> dtoList) {

        String sql = "INSERT IGNORE INTO business (business_name, registration_number, post_code, road_address, industry_name, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int batchSize = 1000;
            int count = 0;

            for (OpenApiBusinessDto dto : dtoList) {

                pstmt.setString(1, dto.getBusinessName());
                pstmt.setString(2, dto.getRegistrationNumber());
                pstmt.setString(3, dto.getPostCode());
                pstmt.setString(4, dto.getRoadAddress());
                pstmt.setString(5, dto.getIndustryName());
                pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

                pstmt.addBatch();
                count++;

                if (count % batchSize == 0) {
                    pstmt.executeBatch(); // 🔥 배치 실행
                    pstmt.clearBatch();
                    System.out.println("✅ Executed batch of " + batchSize);
                }
            }

            // 남은 레코드 처리
            if (count % batchSize != 0) {
                pstmt.executeBatch();
                pstmt.clearBatch();
                System.out.println("✅ Executed final batch of " + (count % batchSize));
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ JDBC insert 실패: " + e.getMessage(), e);
        }
    }

    public void batchInsertMonthlyData(List<BusinessMonthlyDataDto> dataList) {
        String sql = "INSERT IGNORE INTO business_monthly_data " +
                "(business_id, report_month, new_members, resigned_members, billing_amount, status_code) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int batchSize = 1000;
            int count = 0;

            for (BusinessMonthlyDataDto dto : dataList) {
                pstmt.setLong(1, dto.getBusinessId());
                pstmt.setString(2, dto.getReportMonth().replace("-", "")); // "2023-12" → "202312"
                pstmt.setInt(3, dto.getNewMembers());
                pstmt.setInt(4, dto.getResignedMembers());
                pstmt.setBigDecimal(5, dto.getBillingAmount());
                pstmt.setString(6, dto.getStatusCode());

                pstmt.addBatch();
                count++;

                if (count % batchSize == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                    System.out.println("✅ Executed BusinessMonthlyDataDto batch of " + batchSize);
                }
            }

            if (count % batchSize != 0) {
                pstmt.executeBatch();
                pstmt.clearBatch();
                System.out.println("✅ Executed BusinessMonthlyDataDto final batch of " + (count % batchSize));
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ 월별 데이터 insert 실패: " + e.getMessage(), e);
        }
    }
}
