package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class MessageTypeService {
    protected final MessageTypeRepository messageTypeRepository;

    @Autowired
    public MessageTypeService(MessageTypeRepository messageTypeRepository) {
        this.messageTypeRepository = messageTypeRepository;
    }

    public Optional<MessageType> findDataById(Long id) {
        return messageTypeRepository.findById(id);
    }
}
