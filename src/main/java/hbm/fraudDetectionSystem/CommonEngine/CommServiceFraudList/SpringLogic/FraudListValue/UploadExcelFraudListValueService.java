package hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList.FraudList;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudList.FraudListService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class UploadExcelFraudListValueService {
    protected final Logger LOGGER = LoggerFactory.getLogger("U-F-VALUE");
    private final FraudListService listService;
    private final FraudValueRepository fraudValueRepository;

    @Autowired
    public UploadExcelFraudListValueService(FraudListService listService, FraudValueRepository fraudValueRepository) {
        this.listService = listService;
        this.fraudValueRepository = fraudValueRepository;
    }

    public boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public List<FraudValue> getFraudValueDataFromExcel(long listId, String author, InputStream inputStream) {
        List<FraudValue> fraudValues = new ArrayList<>();
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("FraudValue");

            if (sheet == null) {
                LOGGER.error("Sheet not valid");
                throw new RuntimeException("Sheet not valid");
            }

            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                FraudValue fraudValue = new FraudValue();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    switch (cellIndex) {
                        case 0:
                            fraudValue.setValue(this.getValue(cell));
                            break;

                        default: break;
                    }
                    cellIndex++;
                }
                FraudList fraudList = validateFraudList(listId);
                fraudValue.setListId(fraudList);
                fraudValue.setAuthor(author);
                fraudValue.setCreationDate(ts);

                if (!this.validateValue(listId, fraudValue.getValue()))
                    fraudValues.add(fraudValue);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return fraudValues;
    }

    private FraudList validateFraudList(long listId) {
        FraudList fraudList = listService.findListId(listId);
        if (fraudList == null) {
            throw new RuntimeException("list not found by id: " + listId);
        }
        return fraudList;
    }

    protected boolean validateValue(long listId, String value) {
        AtomicBoolean stat = new AtomicBoolean(false);

        this.fraudValueRepository.findByValueAndListIdListId(value, listId)
                .ifPresent(v1 -> {
                    if (v1.getId() != (long) 0) {
                        LOGGER.error(
                                String.format(
                                        "Data already exist: \n\tValue: [%s] \n\tListId: [%s]",
                                        value, listId
                                )
                        );

                        stat.set(true);
                    }
                });

        return stat.get();
    }

    protected String getValue(Cell cell) {
        DataFormatter fmt = new DataFormatter();

        return fmt.formatCellValue(cell);
    }
}
