package hbm.fraudDetectionSystem.ReportEngine.SpringLogic.JasperReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JReportRepository extends JpaRepository<JReport, Long> {
    JReport findAllByReportId(long reportId);

    List<JReport> findAllByOrderByReportNameAsc();
}
