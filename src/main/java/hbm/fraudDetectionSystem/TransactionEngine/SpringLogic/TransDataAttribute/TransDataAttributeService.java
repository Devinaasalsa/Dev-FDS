package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList.FraudList;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListType.FraudListType;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import hbm.fraudDetectionSystem.GeneralComponent.Exception.DataAlreadyExistException;
import hbm.fraudDetectionSystem.GeneralComponent.Exception.DataNotFoundWhenUpdate;
import hbm.fraudDetectionSystem.GeneralComponent.Exception.MsgConfigurationNotFoundException;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class TransDataAttributeService {
    @PersistenceContext
    protected EntityManager em;
    protected final TransDataAttributeRepository transDataAttributeRepository;
    protected final Map<String, MessageConfiguration> isoConfigurations;


    @Autowired
    public TransDataAttributeService(TransDataAttributeRepository transDataAttributeRepository, Map<String, MessageConfiguration> isoConfigurations) {
        this.transDataAttributeRepository = transDataAttributeRepository;
        this.isoConfigurations = isoConfigurations;
    }

    public List<TransDataAttribute> fetchAllAttr() {
        return transDataAttributeRepository.findByOrderByAttribute();
    }

    public List<TransDataAttribute> fetchAllAttrCommon() {
        return transDataAttributeRepository.findAllByAddtData();
    }

    public List<TransDataAttribute> fetchAllByConfigIdUI(long configId) {
        return transDataAttributeRepository.findAllByConfigIdConfigIdOrderByAttribute(configId);
    }

    public List<TransDataAttribute> fetchAllByEndpointIdUI(long endpointId) {
        return transDataAttributeRepository.findAllByEndpointEndpointIdOrderByAttribute(endpointId);
    }

    public List<TransDataAttribute> fetchAllByConfigId(long configId) {
        return transDataAttributeRepository.findAllByConfigId(configId);
    }

    public List<TransDataAttribute> fetchAllByConfigIdAndEndpointId(long configId, long endpointId) {
        return transDataAttributeRepository.findAllByConfigIdAndEndpointId(configId, endpointId);
    }

    public void saveData(TransDataAttribute requestData) throws DataAlreadyExistException, MsgConfigurationNotFoundException {
        if (requestData.getConfigId() != null)
            if (!isConfigIdIsPresent(requestData.getConfigId().getConfigId().toString())) {
                throw new MsgConfigurationNotFoundException();
            }

        if (isDataPresent(requestData)) {
            throw new DataAlreadyExistException();
        } else {
            if (requestData.getConfigId() != null && requestData.getEndpoint() != null)
                transDataAttributeRepository.saveData(requestData.getAttribute(), requestData.getFieldTag(), requestData.getConfigId().getConfigId(), requestData.getDescription(), requestData.getEndpoint().getEndpointId());
            else
                transDataAttributeRepository.saveData(requestData.getAttribute(), requestData.getFieldTag(), requestData.getConfigId().getConfigId(), requestData.getDescription(), null);
        }
    }

    public void updateData(TransDataAttribute requestData) throws DataAlreadyExistException, DataNotFoundWhenUpdate, MsgConfigurationNotFoundException {
        if (requestData.getConfigId() != null)
            if (!isConfigIdIsPresent(requestData.getConfigId().getConfigId().toString())) {
                throw new MsgConfigurationNotFoundException();
            }

        if (isDataStillPresent(requestData.getAttrId())) {
            if (isDataPresentWhenUpdate(requestData)) {
                throw new DataAlreadyExistException();
            } else {
                transDataAttributeRepository.save(requestData);
            }
        } else throw new DataNotFoundWhenUpdate(requestData.getAttrId());
    }

    public void deleteData(long id) {
        transDataAttributeRepository.deleteById(id);
    }

    public List<TransDataAttribute> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<TransDataAttribute> query = cb.createQuery(TransDataAttribute.class);
        Root<TransDataAttribute> root = query.from(TransDataAttribute.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            switch (key) {
                                case "configId":
                                    Long configId = Long.parseLong(value.toString());
                                    Join<TransDataAttribute, MessageConfiguration> configJoin = root.join("configId");
                                    predicates.add(cb.equal(configJoin.get("configId"), configId));
                                    break;

                                case "endpointId":
                                    int endpointId = Integer.parseInt(value.toString());
                                    Join<TransDataAttribute, FraudListType> endpointJoin = root.join("endpoint");
                                    predicates.add(cb.equal(endpointJoin.get("endpointId"), endpointId));
                                    break;

                                case "description":
                                    String likeValue = "%" + value + "%";
                                    predicates.add(cb.like(root.get(key), likeValue));
                                    break;

                                default:
                                    predicates.add(cb.equal(root.get(key), value));
                                    break;
                            }
                        }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<TransDataAttribute> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    public boolean isConfigIdIsPresent(String configId) {
        return isoConfigurations.get(configId) != null;
    }

    public boolean isDataPresent(TransDataAttribute requestData) {
        return findByAttributeAndConfigIdAndAddtData(requestData).isPresent();
    }

    public boolean isDataStillPresent(Long id) {
        return transDataAttributeRepository.findById(id).isPresent();
    }

    public boolean isDataPresentWhenUpdate(TransDataAttribute requestData) {
        return findByAttributeAndConfigIdAndAddtData(requestData)
                .filter(transDataAttribute -> !Objects.equals(transDataAttribute.getAttrId(), requestData.getAttrId()))
                .isPresent();
    }

    public Optional<TransDataAttribute> findByAttributeAndConfigIdAndAddtData(TransDataAttribute requestData) {
        if (requestData.getConfigId() != null)
            return transDataAttributeRepository.findByAttributeAndConfigIdConfigIdAndAddtData(requestData.getAttribute(), requestData.getConfigId().getConfigId(), requestData.getAddtData());
        else
            return transDataAttributeRepository.findByAttributeAndConfigIdConfigIdAndAddtData(requestData.getAttribute(), null, requestData.getAddtData());
    }
}
