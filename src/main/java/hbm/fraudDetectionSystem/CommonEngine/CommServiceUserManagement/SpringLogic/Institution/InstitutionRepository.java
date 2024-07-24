package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Institution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    Institution findInstitutionByInstitutionName(String institutionName);
    List<Institution> findByOrderByIdAsc();
}
