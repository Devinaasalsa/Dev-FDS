package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.SpringLogic.CaseHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseHistoryRepository extends JpaRepository<CaseHistory,Long> {
    List<CaseHistory> findByCaseIdOrderByActionDateDesc(long caseId);
}
