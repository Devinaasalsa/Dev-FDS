package hbm.fraudDetectionSystem.ReportEngine.SpringLogic.JasperReport;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("jReport")
public class JReportResource extends ResponseResourceEntity<Object> {
    protected final JReportService jReportService;

    @Autowired
    public JReportResource(JReportService jReportService) {
        this.jReportService = jReportService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> fetchAll() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<JReport> fetchedData = jReportService.findAll();
            httpStatus = OK;
            httpMessage = "Rules Fetched Successfully";
            return responseWithDataObject(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @GetMapping("export/{reportId}")
    public ResponseEntity<?> exportRuleId(
            @PathVariable("reportId") long reportId,
            @RequestParam("format") String format,
            @RequestParam("username") String username,
            @RequestParam("startDate") Timestamp reportStartDate,
            @RequestParam("endDate")Timestamp reportEndDate
    ) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            JReport report = this.jReportService.findById(reportId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            if (format.equalsIgnoreCase("xls")) {
                headers.setContentDispositionFormData("attachment", report.getReportName() + new Date().toString().replaceAll("[/:]", "-") + ".xlsx");
            }
            if (format.equalsIgnoreCase("pdf")) {
                headers.setContentDispositionFormData("attachment", report.getReportName() + ".pdf");
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(this.jReportService.exportReport(
                            report, format, username, reportStartDate, reportEndDate
                    ));
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("import")
    public ResponseEntity<?>importReport(@RequestParam("file") MultipartFile file, @RequestParam("filename") String filename) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            jReportService.importReport(file, filename);
            httpStatus = OK;
            httpMessage = "Rules Fetched Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            jReportService.deleteById(id);
            httpStatus = OK;
            httpMessage = "J Report Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }
}
