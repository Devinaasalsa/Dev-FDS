package hbm.fraudDetectionSystem.CommonEngine.CommServiceWhiteList.SpringLogic;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class UploadExcelWhiteListService {
    protected final Logger LOGGER = LoggerFactory.getLogger("U-WHITE");
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final FraudWhiteListRepository fraudWhiteListRepository;
    private final TransDataAttributeRepository transDataAttributeRepository;

    @Autowired
    public UploadExcelWhiteListService(UserGroupRepository userGroupRepository, UserRepository userRepository, FraudWhiteListRepository fraudWhiteListRepository, TransDataAttributeRepository transDataAttributeRepository) {
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.fraudWhiteListRepository = fraudWhiteListRepository;
        this.transDataAttributeRepository = transDataAttributeRepository;
    }

    public boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public List<FraudWhiteList> getWhiteListDataFromExcel(InputStream inputStream, long initiatorId, long uGroupId) throws IOException {
        List<FraudWhiteList> fraudWhiteLists = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheet("WhiteList");

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
            FraudWhiteList whiteList = new FraudWhiteList();
            this.checkUserData(whiteList, initiatorId, uGroupId);

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
                        whiteList.setEntityType(getValue(cell));
                        break;

                    case 1:
                        whiteList.setValue(getValue(cell));
                        break;

                    case 2:
                        whiteList.setDateIn(Timestamp.valueOf(cell.getLocalDateTimeCellValue()));
                        break;

                    case 3:
                        whiteList.setDateOut(Timestamp.valueOf(cell.getLocalDateTimeCellValue()));
                        break;

                    case 4:
                        whiteList.setReason(getValue(cell));
                        break;

                    default:
                        break;
                }
                cellIndex++;
            }

            if (!this.validateETypeAndValue(whiteList.getEntityType(), whiteList.getValue()))
                fraudWhiteLists.add(whiteList);
        }

        return fraudWhiteLists;
    }

    protected void checkUserData(FraudWhiteList whiteList, long initiatorId, long uGroupId) {
        whiteList.setInitiator(
                this.userRepository.findById(initiatorId).orElseThrow(() -> new RuntimeException("User not found"))
        );

        whiteList.setUserGroup(
                this.userGroupRepository.findById(uGroupId).orElseThrow(() -> new RuntimeException("User group not found"))
        );
    }

    protected boolean validateETypeAndValue(String eType, String value) {
        AtomicBoolean stat = new AtomicBoolean(false);

        this.fraudWhiteListRepository.findByEntityTypeAndValue(eType, value)
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
