package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageTypeRepository extends JpaRepository<MessageType, Long> {

}
