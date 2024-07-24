package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SubFieldConfiguration;

import hbm.fraudDetectionSystem.GeneralComponent.Exception.*;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.container.ISOFieldContainer;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldConfiguration.FieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldConfiguration.FieldConfigurationRepository;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldEncodingDesc.FieldEncodingDesc;
import hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldFormatDesc.FieldFormatDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.SpringLogicHelper.getChildConstraint;

@Service
@Transactional
public class SubFieldConfigurationService {
    private final SubFieldConfigurationRepository subFieldConfigurationRepository;
    protected final FieldConfigurationRepository fieldConfigurationRepository;
    protected final Map<Long, FieldEncodingDesc> fieldEncoding;
    protected final Map<Long, FieldFormatDesc> fieldFormat;
    protected final Map<String, List<FieldConfiguration>> fieldConfigurations;
    protected final Map<String, ISOFieldContainer[]> fieldConfigurationContainer;

    @Autowired
    public SubFieldConfigurationService(SubFieldConfigurationRepository subFieldConfigurationRepository, FieldConfigurationRepository fieldConfigurationRepository, Map<Long, FieldEncodingDesc> fieldEncoding, Map<Long, FieldFormatDesc> fieldFormat, Map<String, List<FieldConfiguration>> fieldConfigurations, Map<String, ISOFieldContainer[]> fieldConfigurationContainer) {
        this.subFieldConfigurationRepository = subFieldConfigurationRepository;
        this.fieldConfigurationRepository = fieldConfigurationRepository;
        this.fieldEncoding = fieldEncoding;
        this.fieldFormat = fieldFormat;
        this.fieldConfigurations = fieldConfigurations;
        this.fieldConfigurationContainer = fieldConfigurationContainer;
    }

    public List<SubFieldConfiguration> findAllData() {
        return subFieldConfigurationRepository.findAllByOrderByIdAsc();
    }

    public List<SubFieldConfiguration> findByDataId(long dataId) {
        List<SubFieldConfiguration> fetchedData = subFieldConfigurationRepository.findAllByParentIdIdOrderByFieldIdAsc(dataId);
        return fetchedData;
    }

    public void saveData(SubFieldConfiguration requestData) throws DataAlreadyExistException, EncodingNotFoundException, MsgConfigurationNotFoundException, FormatNotFoundException, SubFieldConstraintException, DataNotFoundWhenUpdate {
        if (isParentIdPresent(requestData.getParentId().getId())) {
            if (isDataPresent(requestData)) {
                throw new DataAlreadyExistException();
            } else {
                if (isAllRelationInParentExist(requestData)) {
//                    setChildFK(requestData);
//                    if (subFieldConfigurationService.isChildInGoodCondition(requestData.getSubFieldConfigurations()) == 0) {
//                        fieldConfigurationRepository.save(requestData);
//                    }
                    boolean doesParentHaveSubfield = subFieldConfigurationRepository.findAllByParentIdIdOrderByFieldIdAsc(requestData.getParentId().getId()).size() == 0;

                    subFieldConfigurationRepository.save(requestData);

                    if (doesParentHaveSubfield) {
                        FieldConfiguration parentField = requestData.getParentId();
                        parentField.setHasChild(true);
                        fieldConfigurationRepository.save(parentField);
                    }
                }
            }
        } else throw new MsgConfigurationNotFoundException();
    }

    public void updateData(SubFieldConfiguration requestData) throws MsgConfigurationNotFoundException, DataAlreadyExistException, DataNotFoundWhenUpdate, EncodingNotFoundException, FormatNotFoundException, SubFieldConstraintException {
        if (isParentIdPresent(requestData.getParentId().getId())) {
            if (isDataStillPresent(requestData.getId())) {
                if (isDataPresentWhenUpdate(requestData)) {
                    throw new DataAlreadyExistException();
                } else {
                    if (isAllRelationInParentExist(requestData)) {
//                        setChildFK(requestData);
//                        if (subFieldConfigurationService.isChildInGoodCondition(requestData.getSubFieldConfigurations()) == 0)
//                            fieldConfigurationRepository.save(requestData);
                        subFieldConfigurationRepository.save(requestData);
                    }
                }
            } else throw new DataNotFoundWhenUpdate(requestData.getId());
        } else throw new MsgConfigurationNotFoundException();
    }

    public void deleteData(Long id) {
        Optional<SubFieldConfiguration> deletedData = subFieldConfigurationRepository.findById(id);
        if (deletedData.isPresent()) {
            subFieldConfigurationRepository.deleteById(id);

            boolean doesParentHaveSubfield = subFieldConfigurationRepository.findAllByParentIdIdOrderByFieldIdAsc(deletedData.get().getParentId().getId()).size() == 0;
            if (doesParentHaveSubfield) {
                FieldConfiguration parentField = deletedData.get().getParentId();
                parentField.setHasChild(false);
                parentField.setSubFieldConfigurations(new LinkedList<>());
                fieldConfigurationRepository.save(parentField);
            }
        }
    }


    public int isChildInGoodCondition(List<SubFieldConfiguration> childData) throws EncodingNotFoundException, FormatNotFoundException, SubFieldConstraintException, DataNotFoundWhenUpdate {
        long status = isDataStillPresentWhenUpdate(childData);
        if (status == 0) {
            List<SubFieldConfiguration> constraintChildData = getConstraintChildData(childData);
            if (isChildConstraint(constraintChildData)) {
                throw new SubFieldConstraintException(constraintChildData.size());
            } else {
                if (isAllRelationInChildExist(childData)) {
                    return constraintChildData.size();
                } else return 0;
            }
        } else throw new DataNotFoundWhenUpdate(status);
    }

    protected List<SubFieldConfiguration> getConstraintChildData(List<SubFieldConfiguration> childData) {
        return getChildConstraint(childData);
    }

    protected boolean isChildConstraint(List<SubFieldConfiguration> childData) {
        return childData.size() > 0;
    }

    protected boolean isAllRelationInChildExist(List<SubFieldConfiguration> requestData) throws EncodingNotFoundException, FormatNotFoundException {
        boolean status = false;
        for (SubFieldConfiguration childData : requestData) {
            status = isEncodingPresent(childData.getEncodingId().getEncodingId()) &&
                    isFormatPresent(childData.getFormatId().getFormatId());
        }
        return status;
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

    protected long isDataStillPresentWhenUpdate(List<SubFieldConfiguration> childData) {
        long status = 0;
        for (SubFieldConfiguration data : childData) {
            if (data.getId() != null) {
                if (subFieldConfigurationRepository.findById(data.getId()).isEmpty()) {
                    status = data.getId();
                    break;
                }
            }
        }
        return status;
    }

    protected boolean isAllRelationInParentExist(SubFieldConfiguration requestData) throws MsgConfigurationNotFoundException, EncodingNotFoundException, FormatNotFoundException {
        return isParentIdPresent(requestData.getParentId().getId()) &&
                isEncodingPresent(requestData.getEncodingId().getEncodingId()) &&
                isFormatPresent(requestData.getFormatId().getFormatId());
    }

    protected boolean isParentIdPresent(Long configId) throws MsgConfigurationNotFoundException {
        if (isDataStillPresentFieldConfig(configId)) {
            return true;
        } else throw new MsgConfigurationNotFoundException();
    }

    public boolean isDataStillPresentFieldConfig(Long id) {
        return fieldConfigurationRepository.findById(id).isPresent();
    }

    protected boolean isDataPresent(SubFieldConfiguration requestData) {
        return findDataByFieldIdAndConfigIdAndPriority(requestData).isPresent();
    }

    public boolean isDataStillPresent(Long id) {
        return subFieldConfigurationRepository.findById(id).isPresent();
    }

    protected boolean isDataPresentWhenUpdate(SubFieldConfiguration requestData) {
        return findDataByFieldIdAndConfigIdAndPriority(requestData)
                .filter(fieldConfigurations -> !Objects.equals(fieldConfigurations.getId(), requestData.getId()))
                .isPresent();
    }

    private Optional<SubFieldConfiguration> findDataByFieldIdAndConfigIdAndPriority(SubFieldConfiguration requestData) {
        return subFieldConfigurationRepository.findByFieldIdAndPriorityAndParentIdId(requestData.getFieldId(), requestData.getPriority(), requestData.getParentId().getId());
    }
}
