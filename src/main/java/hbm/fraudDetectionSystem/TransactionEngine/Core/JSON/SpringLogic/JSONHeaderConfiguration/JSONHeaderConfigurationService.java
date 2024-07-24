package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONHeaderConfiguration;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Rule.Rule;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.RuleGroup.RuleGroup;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Enum.JSONFieldState;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class JSONHeaderConfigurationService {
    @PersistenceContext
    protected EntityManager em;
    protected final MessageConfigurationRepository messageConfigurationRepository;
    protected final JSONHeaderConfigurationRepository jsonHeaderConfigurationRepository;

    @Autowired
    public JSONHeaderConfigurationService(MessageConfigurationRepository messageConfigurationRepository, JSONHeaderConfigurationRepository jsonHeaderConfigurationRepository) {
        this.messageConfigurationRepository = messageConfigurationRepository;
        this.jsonHeaderConfigurationRepository = jsonHeaderConfigurationRepository;
    }

    public Set<ChannelEndpoint> findAllFieldConfiguration(Long configId) {
        MessageConfiguration msgConfigs = this.messageConfigurationRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Config id not found"));

        for (ChannelEndpoint endpoint : msgConfigs.getEndpoints()) {
            if (endpoint.getIsAuth()) {
                endpoint.setType("AUTH");
            } else if (endpoint.getSysMti().equals("0400")) {
                endpoint.setType("REVERSAL");
            } else {
                endpoint.setType("ORIGINAL");
            }

            List<Map<String, Object>> listStates = new LinkedList<>();

            listStates.add(this.fetchStates(endpoint, JSONFieldState.REQUEST));
            listStates.add(this.fetchStates(endpoint, JSONFieldState.RESPONSE));
            endpoint.setStates(listStates);
        }

        return msgConfigs.getEndpoints();
    }

    public void saveData(JSONHeaderConfiguration data) {
        this.validateDataAlreadyPresent(data);
        this.jsonHeaderConfigurationRepository.save(data);
    }

    public void updateData(JSONHeaderConfiguration data) {
        this.validateDataId(data.getId());
        this.validateDataAlreadyPresent(data);
        this.jsonHeaderConfigurationRepository.save(data);
    }

    public void deleteData(long id) {
        this.jsonHeaderConfigurationRepository.deleteById(id);
    }


    protected List<JSONHeaderConfiguration> findFieldByState(long configId, long endpointId, JSONFieldState state) {
        return this.jsonHeaderConfigurationRepository
                .findAllByMsgConfigConfigIdAndEndpointEndpointIdAndStateOrderBySequenceAsc(configId, endpointId, state);
    }

    protected Map<String, Object> fetchStates(ChannelEndpoint endpoint, JSONFieldState state) {
        Map<String, Object> listConfigs = new LinkedHashMap<>();
        List<JSONHeaderConfiguration> v1 = this.findFieldByState(endpoint.getConfigId(), endpoint.getEndpointId(), state);

        for (JSONHeaderConfiguration v2 : v1) {
            //This is for prevent the never end loop data
            this.em.detach(v2);
            v2.setMsgConfig(null);
            v2.setEndpoint(null);
        }

        switch (state) {
            case REQUEST:
                listConfigs.put("state", "Request");
                listConfigs.put("key", endpoint.getEndpointId() + " - Request");
                break;

            case RESPONSE:
                listConfigs.put("state", "Response");
                listConfigs.put("key", endpoint.getEndpointId() + " - Response");
                break;
        }
        listConfigs.put("configs", v1);

        return listConfigs;
    }

    public List<JSONHeaderConfiguration> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<JSONHeaderConfiguration> query = cb.createQuery(JSONHeaderConfiguration.class);
        Root<JSONHeaderConfiguration> root = query.from(JSONHeaderConfiguration.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                        if (!value.toString().isEmpty()) {
                            if (key.equals("fieldName")) {
                                String likeValue = "%" + value + "%";
                                predicates.add(cb.like(root.get(key), likeValue));
                            }
                        }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<JSONHeaderConfiguration> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    protected void validateDataAlreadyPresent(JSONHeaderConfiguration data) {
        this.jsonHeaderConfigurationRepository
                .findByFieldNameAndSequenceAndEndpointEndpointIdAndMsgConfigConfigId(
                        data.getFieldName(), data.getSequence(),
                        data.getEndpoint().getEndpointId(),
                        data.getMsgConfig().getConfigId()
                ).ifPresent(v1 -> {
                    if (!Objects.equals(v1.getId(), data.getId()))
                        throw new RuntimeException("Data already exist");
                });

    }

    protected void validateDataId(long id) {
        this.jsonHeaderConfigurationRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Existing data not found"));
    }
}
