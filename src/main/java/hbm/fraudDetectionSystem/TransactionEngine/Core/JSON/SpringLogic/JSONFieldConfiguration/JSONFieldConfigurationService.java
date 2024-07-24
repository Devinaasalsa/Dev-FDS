package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Enum.JSONFieldState;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONHeaderConfiguration.JSONHeaderConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class JSONFieldConfigurationService {
    @PersistenceContext
    protected EntityManager em;
    protected final MessageConfigurationRepository messageConfigurationRepository;
    protected final JSONFieldConfigurationRepository jsonFieldConfigurationRepository;

    @Autowired
    public JSONFieldConfigurationService(MessageConfigurationRepository messageConfigurationRepository, JSONFieldConfigurationRepository jsonFieldConfigurationRepository) {
        this.messageConfigurationRepository = messageConfigurationRepository;
        this.jsonFieldConfigurationRepository = jsonFieldConfigurationRepository;
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

    public void saveData(JSONFieldConfiguration data) {
        this.validateDataAlreadyPresent(data);
        this.jsonFieldConfigurationRepository.save(data);
    }

    public void updateData(JSONFieldConfiguration data) {
        this.validateDataId(data.getId());
        this.validateDataAlreadyPresent(data);
        this.jsonFieldConfigurationRepository.save(data);
    }

    public void deleteData(long id) {
        this.jsonFieldConfigurationRepository.deleteById(id);
    }


    protected int getLowLevel(ChannelEndpoint endpoint, int state) {
        return this.jsonFieldConfigurationRepository.getLowLevel(endpoint.getEndpointId(), endpoint.getConfigId(), state);
    }

    protected List<JSONFieldConfiguration> findFieldByLevel(long configId, long endpointId, JSONFieldState state, int level) {
        return this.jsonFieldConfigurationRepository
                .findAllByMsgConfigConfigIdAndEndpointEndpointIdAndStateAndLevelOrderByParentFieldAscSequenceAsc(configId, endpointId, state, level);
    }

    protected Map<String, Object> fetchStates(ChannelEndpoint endpoint, JSONFieldState state) {
        Map<String, Object> listConfigs = new LinkedHashMap<>();
        List<JSONFieldConfiguration> topParent = new LinkedList<>();
        Map<Long, List<JSONFieldConfiguration>> tempChildCol = new LinkedHashMap<>();
        int lowLevel = this.getLowLevel(endpoint, state.ordinal());

        for (int i = lowLevel; i > 0; i--) {
            List<JSONFieldConfiguration> v1 = this.findFieldByLevel(endpoint.getConfigId(), endpoint.getEndpointId(), state, i);

            for (JSONFieldConfiguration v2 : v1) {

                //This is for prevent the never end loop data
                this.em.detach(v2);
                v2.setMsgConfig(null);
                v2.setEndpoint(null);
                List<JSONFieldConfiguration> currField = tempChildCol.get(v2.getId());

                if (currField != null) {
                    v2.setChildField(currField);
                }
            }

            if (i != 1) {
                tempChildCol.putAll(
                        v1
                                .stream()
                                .collect(Collectors.groupingBy(JSONFieldConfiguration::getParentField))
                );
            } else {
                //This block will run when the field are the top or in level 1
                topParent = v1;
            }
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
        listConfigs.put("configs", topParent);

        return listConfigs;
    }

    public List<JSONFieldConfiguration> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<JSONFieldConfiguration> query = cb.createQuery(JSONFieldConfiguration.class);
        Root<JSONFieldConfiguration> root = query.from(JSONFieldConfiguration.class);

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

        TypedQuery<JSONFieldConfiguration> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    protected void validateDataAlreadyPresent(JSONFieldConfiguration data) {
        if (data.getParentField() == null) {
            this.jsonFieldConfigurationRepository
                    .findByFieldNameAndLevelAndParentFieldIsNullAndSequenceAndEndpointEndpointIdAndMsgConfigConfigId(
                            data.getFieldName(),
                            data.getLevel(), data.getSequence(),
                            data.getEndpoint().getEndpointId(), data.getMsgConfig().getConfigId()
                    ).ifPresent(v1 -> {
                        if (!Objects.equals(v1.getId(), data.getId()))
                            throw new RuntimeException("Data already exist");
                    });
        } else {
            this.jsonFieldConfigurationRepository
                    .findByFieldNameAndLevelAndParentFieldAndSequenceAndEndpointEndpointIdAndMsgConfigConfigId(
                            data.getFieldName(),
                            data.getLevel(), data.getParentField(),
                            data.getSequence(), data.getEndpoint().getEndpointId(),
                            data.getMsgConfig().getConfigId()
                    ).ifPresent(v1 -> {
                        if (!Objects.equals(v1.getId(), data.getId()))
                            throw new RuntimeException("Data already exist");
                    });
        }
    }

    protected void validateDataId(long id) {
        this.jsonFieldConfigurationRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Existing data not found"));
    }
}
