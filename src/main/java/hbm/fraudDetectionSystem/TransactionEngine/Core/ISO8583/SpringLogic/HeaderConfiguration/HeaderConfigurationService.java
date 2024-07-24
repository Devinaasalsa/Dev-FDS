package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.HeaderConfiguration;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic.FraudWhiteList;
import hbm.fraudDetectionSystem.GeneralComponent.Exception.*;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldConfiguration.FieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldEncodingDesc.FieldEncodingDesc;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldFormatDesc.FieldFormatDesc;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Qualifier("ISOHeaderConfigurationService")
@Transactional(rollbackOn = Exception.class)
public class HeaderConfigurationService {
    @PersistenceContext
    private EntityManager em;
    protected final HeaderConfigurationRepository headerConfigurationRepository;
    protected final Map<String, MessageConfiguration> isoConfigurations;
    protected final Map<Long, FieldEncodingDesc> fieldEncoding;
    protected final Map<Long, FieldFormatDesc> fieldFormat;
    protected final Map<String, List<HeaderConfiguration>> headerConfigurations;
    protected final Map<String, ISOFieldContainer[]> headerConfigurationContainer;

    @Autowired
    public HeaderConfigurationService(HeaderConfigurationRepository headerConfigurationRepository, Map<String, MessageConfiguration> isoConfigurations, Map<Long, FieldEncodingDesc> fieldEncoding, Map<Long, FieldFormatDesc> fieldFormat, Map<String, List<HeaderConfiguration>> headerConfigurations, Map<String, ISOFieldContainer[]> headerConfigurationContainer) {
        this.headerConfigurationRepository = headerConfigurationRepository;
        this.isoConfigurations = isoConfigurations;
        this.fieldEncoding = fieldEncoding;
        this.fieldFormat = fieldFormat;
        this.headerConfigurations = headerConfigurations;
        this.headerConfigurationContainer = headerConfigurationContainer;
    }

    public List<HeaderConfiguration> findAllData(long id) {
        return headerConfigurationRepository.findAllByConfigIdConfigIdOrderByConfigIdAscFieldIdAscPriorityAsc(id);
    }

    public void saveData(HeaderConfiguration requestData) throws DataAlreadyExistException, EncodingNotFoundException, MsgConfigurationNotFoundException, FormatNotFoundException {
        if (isConfigIdIsPresent(requestData.getConfigId().getConfigId().toString())) {
            if (isDataPresent(requestData)) {
                throw new DataAlreadyExistException();
            } else {
                if (isAllParentExist(requestData))
                    headerConfigurationRepository.save(
                            requestData
                    );
            }
        } else throw new MsgConfigurationNotFoundException();
    }

    public void updateData(HeaderConfiguration requestData) throws MsgConfigurationNotFoundException, DataAlreadyExistException, DataNotFoundWhenUpdate, EncodingNotFoundException, FormatNotFoundException {
        if (isConfigIdPresent(requestData.getConfigId().getConfigId())) {
            if (isDataStillPresent(requestData.getId())) {
                if (isDataPresentWhenUpdate(requestData)) {
                    throw new DataAlreadyExistException();
                } else {
                    if (isAllParentExist(requestData))
                        headerConfigurationRepository.save(requestData);
                }
            } else throw new DataNotFoundWhenUpdate(requestData.getId());
        } else throw new MsgConfigurationNotFoundException();
    }

    public void deleteData(Long id) {
        headerConfigurationRepository.deleteById(id);
    }

    public List<HeaderConfiguration> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<HeaderConfiguration> query = cb.createQuery(HeaderConfiguration.class);
        Root<HeaderConfiguration> root = query.from(HeaderConfiguration.class);

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

        TypedQuery<HeaderConfiguration> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    public boolean isConfigIdIsPresent(String configId) {
        return isoConfigurations.get(configId) != null;
    }

    public boolean isDataPresent(HeaderConfiguration requestData) {
        return findDataByFieldIdAndConfigIdAndPriority(requestData).isPresent();
    }

    public boolean isDataPresentWhenUpdate(HeaderConfiguration requestData) {
        return findDataByFieldIdAndConfigIdAndPriority(requestData)
                .filter(headerConfigurations -> !Objects.equals(headerConfigurations.getId(), requestData.getId()))
                .isPresent();
    }

    public boolean isDataStillPresent(Long id) {
        return headerConfigurationRepository.findById(id).isPresent();
    }

    public Optional<HeaderConfiguration> findDataByFieldIdAndConfigIdAndPriority(HeaderConfiguration requestData) {
        return headerConfigurationRepository.findByFieldIdAndPriorityAndConfigIdConfigId(requestData.getFieldId(), requestData.getPriority(), requestData.getConfigId().getConfigId());
    }

    public boolean isAllParentExist(HeaderConfiguration requestData) throws MsgConfigurationNotFoundException, EncodingNotFoundException, FormatNotFoundException {
        return isConfigIdPresent(requestData.getConfigId().getConfigId()) &&
                isEncodingPresent(requestData.getEncodingId().getEncodingId()) &&
                isFormatPresent(requestData.getFormatId().getFormatId());
    }

    public boolean isConfigIdPresent(Long configId) throws MsgConfigurationNotFoundException {
        if (isoConfigurations.get(configId.toString()) != null) {
            return true;
        } else throw new MsgConfigurationNotFoundException();
    }

    public boolean isEncodingPresent(Long encodingId) throws EncodingNotFoundException {
        if (fieldEncoding.get(encodingId) != null) {
            return true;
        } else throw new EncodingNotFoundException(encodingId);
    }

    public boolean isFormatPresent(Long formatId) throws FormatNotFoundException {
        if (fieldFormat.get(formatId) != null) {
            return true;
        } else throw new FormatNotFoundException(formatId);
    }

    public boolean isConfigIdHasHeaderConfiguration(String configId) {
        return headerConfigurations.get(configId) != null;
    }

    public ISOFieldContainer[] getHeaderConfigurationFromMemory(String configId) {
        return headerConfigurationContainer.get(configId);
    }
}
