package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration;

import hbm.fraudDetectionSystem.GeneralComponent.Exception.DataNotFoundWhenUpdate;
import hbm.fraudDetectionSystem.GeneralComponent.Exception.MsgTypeConfigurationNotFoundException;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageType.MessageType;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageType.MessageTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class MessageConfigurationService {
    @PersistenceContext
    private EntityManager em;
    private final MessageConfigurationRepository messageConfigurationRepository;
    protected final MessageTypeService messageTypeService;

    @Autowired
    public MessageConfigurationService(MessageConfigurationRepository messageConfigurationRepository, MessageTypeService messageTypeService) {
        this.messageConfigurationRepository = messageConfigurationRepository;
        this.messageTypeService = messageTypeService;
    }

    public List<MessageConfiguration> findAllData() {
        return messageConfigurationRepository.findAll();
    }

    public List<MessageConfiguration> findAllISO8583Configuration() {
        return messageConfigurationRepository.findAllByMsgType_MsgIdOrderByConfigIdAsc(1);
    }

    public List<MessageConfiguration> findAllJSONConfiguration() {
        return messageConfigurationRepository.findAllByMsgType_MsgIdOrderByConfigIdAsc(2);
    }

    public MessageConfiguration findByDataId(String dataId) {
        return messageConfigurationRepository.findAllByConfigId(Long.valueOf(dataId));
    }

    public Optional<MessageConfiguration> findExistingDataById(Long id) {
        return messageConfigurationRepository.findById(id);
    }

    public MessageConfiguration saveData(MessageConfiguration data) throws MsgTypeConfigurationNotFoundException {
        if (isMessageTypeIsPresent(data.getMsgType().getMsgId())) {
            return messageConfigurationRepository.save(data);
        } else throw new MsgTypeConfigurationNotFoundException();
    }

    public void updateData(MessageConfiguration data) throws DataNotFoundWhenUpdate, MsgTypeConfigurationNotFoundException {
        Optional<MessageConfiguration> fetchedData = messageConfigurationRepository.findById(data.getConfigId());
        if (isMessageTypeIsPresent(data.getMsgType().getMsgId())) {
            if (fetchedData.isPresent()) {
                messageConfigurationRepository.save(data);
            } else throw new DataNotFoundWhenUpdate(data.getConfigId());
        } else throw new MsgTypeConfigurationNotFoundException();
    }

    public void deleteData(Long dataId) {
        messageConfigurationRepository.deleteById(dataId);
    }

    public List<MessageConfiguration> searchConfiguration(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<MessageConfiguration> query = cb.createQuery(MessageConfiguration.class);
        Root<MessageConfiguration> root = query.from(MessageConfiguration.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            if (key.equals("msgType")) {
                                long msgTypeId = Long.parseLong(value.toString());
                                Join<MessageConfiguration, MessageType> msgTypeJoin = root.join("msgType");
                                predicates.add(cb.equal(msgTypeJoin.get("msgId"), msgTypeId));
                            } else if (key.equals("name")) {
                                String likeValue = "%" + value + "%";
                                predicates.add(cb.like(root.get(key), likeValue));
                            } else {
                                predicates.add(cb.equal(root.get(key), value));
                            }
                        }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<MessageConfiguration> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    public boolean isMessageTypeIsPresent(Long typeId) {
        return messageTypeService.findDataById(typeId).isPresent();
    }
}
