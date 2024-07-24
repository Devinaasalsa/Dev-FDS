package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.TemplateSetup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TemplateSetupRepository extends JpaRepository<TemplateSetup, Long> {
    TemplateSetup findByTemplateId(long templateId);
    List<TemplateSetup> findByOrderByTemplateIdAsc();

}
