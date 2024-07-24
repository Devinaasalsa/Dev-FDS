package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserAudit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAuditRepository extends JpaRepository<UserAudit, Long> {
    List<UserAudit> findAllByUserId(Long id);
    List<UserAudit> findByOrderByCaptureDateDesc();
}
