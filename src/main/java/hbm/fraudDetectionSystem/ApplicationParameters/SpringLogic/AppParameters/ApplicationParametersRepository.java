package hbm.fraudDetectionSystem.ApplicationParameters.SpringLogic.AppParameters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationParametersRepository extends JpaRepository<ApplicationParameters, Long> {

    ApplicationParameters findApplicationParametersById(long id);
}
