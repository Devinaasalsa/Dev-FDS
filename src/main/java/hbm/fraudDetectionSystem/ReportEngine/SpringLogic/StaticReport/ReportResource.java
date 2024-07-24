package hbm.fraudDetectionSystem.ReportEngine.SpringLogic.StaticReport;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/report")
public class ReportResource extends ResponseResourceEntity<Report>{
    protected final ReportService reportService;

    @Autowired
    public ReportResource(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/generate")
    public ResponseEntity<?>getAllFraudlist(@RequestParam("reportType")int reportType,
                                            @RequestParam("startDate")Timestamp reportStartDate,
                                            @RequestParam("endDate")Timestamp reportEndDate){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            Report generateReport = reportService.generateReport(reportType, reportStartDate, reportEndDate);
            httpStatus = OK;
            httpMessage = "Report Generated Successfully";
            return responseWithData(httpStatus, httpMessage, generateReport);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            e.printStackTrace();
            return responseWithData(httpStatus, httpMessage, null);
        }
    }

}
