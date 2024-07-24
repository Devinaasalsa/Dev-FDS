package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.RecipientGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RecipientGroupRepository extends JpaRepository<RecipientGroup, Long> {
    RecipientGroup findByGroupId(long groupId);
    List<RecipientGroup> findByOrderByGroupIdAsc();

}
