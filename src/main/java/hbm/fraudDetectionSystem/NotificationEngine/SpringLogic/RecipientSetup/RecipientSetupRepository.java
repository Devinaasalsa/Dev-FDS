package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientSetup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository

public interface RecipientSetupRepository extends JpaRepository<RecipientSetup, Long> {
    RecipientSetup findByRecipientId(long recipientId);

    List<RecipientSetup> findByOrderByRecipientIdAsc();

    void deleteByRecipientId(long recipientId);

    Optional<RecipientSetup> findByRecipientIdAndContactValue(long id, String email);

    List<RecipientSetup> findAllByGroupIdOrderByRecipientIdAsc(long groupId);
}
