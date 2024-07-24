package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList.FraudList;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList.FraudListService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.constant.FraudValueConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudListExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudValueExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.exception.FraudValueNotFound;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class FraudValueServiceImpl implements FraudValueService {
    protected final Logger LOGGER = LoggerFactory.getLogger("U-F-VALUE");

    private FraudValueRepository repository;
    private FraudListService listService;
    private UploadExcelFraudListValueService valueService;

    @Autowired
    public FraudValueServiceImpl(FraudValueRepository repository, FraudListService listService, UploadExcelFraudListValueService valueService) {
        this.repository = repository;
        this.listService = listService;
        this.valueService = valueService;
    }

    @Override
    public FraudValue addValue(String value, String author, Long listId) throws FraudListExistException, FraudValueNotFound, FraudValueExistException {
        this.validateValue(0L, listId, value);

        FraudList getListId = listService.findListId(listId);
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        FraudValue addListValue = new FraudValue();
        addListValue.setValue(value);
        addListValue.setAuthor(author);
        addListValue.setCreationDate(ts);
        addListValue.setListId(getListId);
        repository.save(addListValue);
        return addListValue;
    }

    @Override
    public List<FraudValue> getSanctionListValue() {
        return repository.findByOrderByIdAsc();
    }

    @Override
    public FraudValue updateValue(FraudValue value) {
        this.validateValue(value.getId(), value.getListId().getListId(), value.getValue());
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());

        value.setCreationDate(ts);
        return repository.save(value);
    }

    private FraudValue findByListValue(String newValue) {
        return repository.findSanctionListValueByValue(newValue);
    }

    @Override
    public void deleteFraudListValue(Long id) {
        FraudValue fraudValue = findAllById(id);
        repository.deleteById(fraudValue.getId());
    }

    @Override
    public FraudValue findAllById(long id) {
        return repository.findAllById(id);
    }

    @Override
    public FraudValue findByValue(String value) {
        return repository.findByValue(value);
    }

    @Override
    public List<FraudValue> findAllByListId(long listId) {
        return repository.findAllByListIdListId(listId);
    }

    @Override
    public List<FraudValue> findAllByListName(String listName) {
        return repository.findAllByListId_ListName(listName);
    }

    @Override
    public void saveFraudValueToDatabase(long listId, String author, MultipartFile file) throws IOException {
        if (valueService.isValidExcelFile(file)) {
            LOGGER.info(
                    String.format(
                            "Processing file with name: [%s]",
                            file.getOriginalFilename()
                    )
            );

            List<FraudValue> fraudValues = valueService.getFraudValueDataFromExcel(listId, author, file.getInputStream());

            LOGGER.info(
                    String.format(
                            "Successfully insert data with total: [%s]",
                            repository.saveAll(fraudValues).size()
                    )
            );
        } else {
            throw new RuntimeException("The file isn't valid excel");
        }
    }

    protected void validateValue(long valueId, long listId, String value) {
        this.repository.findByValueAndListIdListId(value, listId)
                .ifPresent(v1 -> {
                    if (v1.getId() != valueId) {
                        throw new RuntimeException("Data already exist");
                    }
                });
    }
}
