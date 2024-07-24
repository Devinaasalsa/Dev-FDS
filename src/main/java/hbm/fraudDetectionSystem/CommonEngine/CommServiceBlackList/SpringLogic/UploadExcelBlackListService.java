package hbm.fraudDetectionSystem.CommonEngine.CommServiceBlackList.SpringLogic;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.UserRepository;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroupRepository;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute.TransDataAttributeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
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
public class UploadExcelBlackListService {
    protected final Logger LOGGER = LoggerFactory.getLogger("U-BLACK");

    protected final UserRepository userRepository;
    protected final UserGroupRepository userGroupRepository;
    protected final FraudBlackListRepository fraudBlackListRepository;
    private final TransDataAttributeRepository transDataAttributeRepository;


    @Autowired
    public UploadExcelBlackListService(UserRepository userRepository, UserGroupRepository userGroupRepository, FraudBlackListRepository fraudBlackListRepository, TransDataAttributeRepository transDataAttributeRepository) {
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
        this.fraudBlackListRepository = fraudBlackListRepository;
        this.transDataAttributeRepository = transDataAttributeRepository;
    }

    public boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public List<FraudBlackList> getBlacklistDataFromExcel(InputStream inputStream, long initiatorId, long uGroupId) throws IOException {
        List<FraudBlackList> fraudBlackLists = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheet("BlackList");

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

            FraudBlackList fraudBlackList = new FraudBlackList();
            this.checkUserData(fraudBlackList, initiatorId, uGroupId);

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cellIndex) {
                    case 0:
                        this.transDataAttributeRepository.findByAttribute(cell.getStringCellValue())
                                .orElseThrow(() -> new RuntimeException(
                                        String.format(
                                                "Attribute [%s] not found",
                                                cell.getStringCellValue()
                                        )
                                ));
                        fraudBlackList.setEntityType(getValue(cell));
                        break;
                    case 1:
                        fraudBlackList.setValue(getValue(cell));
                        break;
                    case 2:
                        fraudBlackList.setDateIn(Timestamp.valueOf(cell.getLocalDateTimeCellValue()));
                        break;
                    case 3:
                        fraudBlackList.setDateOut(Timestamp.valueOf(cell.getLocalDateTimeCellValue()));
                        break;
                    case 4:
                        fraudBlackList.setReason(getValue(cell));
                        break;

                    default:
                        break;
                }
                cellIndex++;
            }

            if (!this.validateETypeAndValue(fraudBlackList.getEntityType(), fraudBlackList.getValue()))
                fraudBlackLists.add(fraudBlackList);
        }
        return fraudBlackLists;
    }

    protected void checkUserData(FraudBlackList blackList, long initiatorId, long uGroupId) {
        blackList.setInitiator(
                this.userRepository.findById(initiatorId).orElseThrow(() -> new RuntimeException("User not found"))
        );

        blackList.setUserGroup(
                this.userGroupRepository.findById(uGroupId).orElseThrow(() -> new RuntimeException("User group not found"))
        );
    }

    protected boolean validateETypeAndValue(String eType, String value) {
        AtomicBoolean stat = new AtomicBoolean(false);

        this.fraudBlackListRepository.findByEntityTypeAndValue(eType, value)
                .ifPresent(v1 -> {
                    if (v1.getId() != (long) 0) {
                        LOGGER.error(
                                String.format(
                                        "Data already exist: \n\tEType: [%s] \n\tValue: [%s]",
                                        eType, value
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
