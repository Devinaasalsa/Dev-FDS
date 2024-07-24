package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldConfiguration;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic.FraudWhiteList;
import hbm.fraudDetectionSystem.GeneralComponent.Exception.*;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldEncodingDesc.FieldEncodingDesc;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldFormatDesc.FieldFormatDesc;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SubFieldConfiguration.SubFieldConfigurationService;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

@Service
@Qualifier("ISOFieldConfigurationService")
@Transactional
public class FieldConfigurationService {
    @PersistenceContext
    private EntityManager em;
    protected final FieldConfigurationRepository fieldConfigurationRepository;
    protected final MessageConfigurationService messageConfigurationService;
    protected final SubFieldConfigurationService subFieldConfigurationService;
    protected final Map<Long, FieldEncodingDesc> fieldEncoding;
    protected final Map<Long, FieldFormatDesc> fieldFormat;
    protected final Map<String, List<FieldConfiguration>> fieldConfigurations;
    protected final Map<String, ISOFieldContainer[]> fieldConfigurationContainer;

    @Autowired
    public FieldConfigurationService(FieldConfigurationRepository fieldConfigurationRepository, MessageConfigurationService messageConfigurationService, SubFieldConfigurationService subFieldConfigurationService, Map<Long, FieldEncodingDesc> fieldEncoding, Map<Long, FieldFormatDesc> fieldFormat, Map<String, List<FieldConfiguration>> fieldConfigurations, Map<String, ISOFieldContainer[]> fieldConfigurationContainer) {
        this.fieldConfigurationRepository = fieldConfigurationRepository;
        this.messageConfigurationService = messageConfigurationService;
        this.subFieldConfigurationService = subFieldConfigurationService;
        this.fieldEncoding = fieldEncoding;
        this.fieldFormat = fieldFormat;
        this.fieldConfigurations = fieldConfigurations;
        this.fieldConfigurationContainer = fieldConfigurationContainer;
    }

    public List<FieldConfiguration> findAllData(long id) {
        return fieldConfigurationRepository.findAllByConfigIdConfigIdOrderByConfigIdAscFieldIdAscPriorityAsc(id);
    }

    public void saveData(FieldConfiguration requestData) throws DataAlreadyExistException, EncodingNotFoundException, MsgConfigurationNotFoundException, FormatNotFoundException, SubFieldConstraintException, DataNotFoundWhenUpdate {
        if (isConfigIdPresent(requestData.getConfigId().getConfigId())) {
            if (isDataPresent(requestData)) {
                throw new DataAlreadyExistException();
            } else {
                if (isAllRelationInParentExist(requestData)) {
//                    setChildFK(requestData);
//                    if (subFieldConfigurationService.isChildInGoodCondition(requestData.getSubFieldConfigurations()) == 0) {
//                        fieldConfigurationRepository.save(requestData);
//                    }
                    fieldConfigurationRepository.save(requestData);
                }
            }
        } else throw new MsgConfigurationNotFoundException();
    }

    public void updateData(FieldConfiguration requestData) throws MsgConfigurationNotFoundException, DataAlreadyExistException, DataNotFoundWhenUpdate, EncodingNotFoundException, FormatNotFoundException, SubFieldConstraintException {
        if (isConfigIdPresent(requestData.getConfigId().getConfigId())) {
            if (isDataStillPresent(requestData.getId())) {
                if (isDataPresentWhenUpdate(requestData)) {
                    throw new DataAlreadyExistException();
                } else {
                    if (isAllRelationInParentExist(requestData)) {
//                        setChildFK(requestData);
//                        if (subFieldConfigurationService.isChildInGoodCondition(requestData.getSubFieldConfigurations()) == 0)
//                            fieldConfigurationRepository.save(requestData);
                        fieldConfigurationRepository.save(requestData);
                    }
                }
            } else throw new DataNotFoundWhenUpdate(requestData.getId());
        } else throw new MsgConfigurationNotFoundException();
    }

    public void deleteData(Long id) {
        fieldConfigurationRepository.deleteById(id);
    }

    public List<FieldConfiguration> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<FieldConfiguration> query = cb.createQuery(FieldConfiguration.class);
        Root<FieldConfiguration> root = query.from(FieldConfiguration.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            switch (key) {
                                case "formatId":
                                    Long formatId = Long.parseLong(value.toString());
                                    Join<FraudWhiteList, FieldFormatDesc> formatJoin = root.join("formatId");
                                    predicates.add(cb.equal(formatJoin.get("formatId"), formatId));
                                    break;

                                case "encodingId":
                                    int encodingId = Integer.parseInt(value.toString());
                                    Join<FraudWhiteList, User> encodingJoin = root.join("encodingId");
                                    predicates.add(cb.equal(encodingJoin.get("encodingId"), encodingId));
                                    break;

                                case "configId":
                                    int configId = Integer.parseInt(value.toString());
                                    Join<FraudWhiteList, User> configJoin = root.join("configId");
                                    predicates.add(cb.equal(configJoin.get("configId"), configId));
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

        TypedQuery<FieldConfiguration> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    protected boolean isDataPresent(FieldConfiguration requestData) {
        return findDataByFieldIdAndConfigIdAndPriority(requestData).isPresent();
    }

    protected boolean isDataPresentWhenUpdate(FieldConfiguration requestData) {
        return findDataByFieldIdAndConfigIdAndPriority(requestData)
                .filter(fieldConfigurations -> !Objects.equals(fieldConfigurations.getId(), requestData.getId()))
                .isPresent();
    }

    public boolean isDataStillPresent(Long id) {
        return fieldConfigurationRepository.findById(id).isPresent();
    }

    private Optional<FieldConfiguration> findDataByFieldIdAndConfigIdAndPriority(FieldConfiguration requestData) {
        return fieldConfigurationRepository.findByFieldIdAndPriorityAndConfigIdConfigId(requestData.getFieldId(), requestData.getPriority(), requestData.getConfigId().getConfigId());
    }

    protected boolean isAllRelationInParentExist(FieldConfiguration requestData) throws MsgConfigurationNotFoundException, EncodingNotFoundException, FormatNotFoundException {
        return isConfigIdPresent(requestData.getConfigId().getConfigId()) &&
                isEncodingPresent(requestData.getEncodingId().getEncodingId()) &&
                isFormatPresent(requestData.getFormatId().getFormatId());
    }

    protected boolean isConfigIdPresent(Long configId) throws MsgConfigurationNotFoundException {
        if (messageConfigurationService.findExistingDataById(configId).isPresent()) {
            return true;
        } else throw new MsgConfigurationNotFoundException();
    }

    protected boolean isEncodingPresent(Long encodingId) throws EncodingNotFoundException {
        if (fieldEncoding.get(encodingId) != null) {
            return true;
        } else throw new EncodingNotFoundException(encodingId);
    }

    protected boolean isFormatPresent(Long formatId) throws FormatNotFoundException {
        if (fieldFormat.get(formatId) != null) {
            return true;
        } else throw new FormatNotFoundException(formatId);
    }

    protected void setChildFK(FieldConfiguration requestData) {
        if (requestData.getSubFieldConfigurations().size() > 0) {
            requestData.getSubFieldConfigurations().forEach(
                    data -> {
                        data.setParentId(requestData);
                    }
            );
        }
    }

    public boolean isConfigIdHasFieldConfiguration(String configId) {
        return fieldConfigurations.get(configId) != null;
    }

    public ISOFieldContainer[] getFieldConfigurationFromMemory(String configId) {
        return fieldConfigurationContainer.get(configId);
    }
}
