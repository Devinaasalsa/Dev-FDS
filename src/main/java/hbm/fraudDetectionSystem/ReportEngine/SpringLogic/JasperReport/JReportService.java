package hbm.fraudDetectionSystem.ReportEngine.SpringLogic.JasperReport;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.User;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User.UserRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hbm.fraudDetectionSystem.ReportEngine.Constant.JReportConstant.PRINTING;

@Service
@Transactional
public class JReportService {
    protected final JReportRepository jReportRepository;
    protected final UserRepository userRepository;
    protected final DataSource dataSource;

    @Autowired
    public JReportService(JReportRepository jReportRepository, UserRepository userRepository, DataSource dataSource) {
        this.jReportRepository = jReportRepository;
        this.userRepository = userRepository;
        this.dataSource = dataSource;
    }

    public List<JReport> findAll() {
        return this.jReportRepository.findAllByOrderByReportNameAsc();
    }

    public JReport findById(long id) {
        return this.jReportRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public JReport importReport(MultipartFile file, String filename) throws IOException {
        byte[] fileContent = file.getBytes();
        JReport jReport = new JReport();
        jReport.setReportName(filename);
        jReport.setJrReport(fileContent);
        return jReportRepository.save(jReport);
    }

    public byte[] exportReport(JReport report, String format, String username, Timestamp reportStartDate, Timestamp reportEndDate) throws SQLException, JRException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", reportStartDate);
        parameters.put("endDate", reportEndDate);

        User user = userRepository.findUserByUsername(username);
        Connection connection = dataSource.getConnection();
        InputStream file = new ByteArrayInputStream(report.getJrReport());
        JasperReport jasperReport = JasperCompileManager.compileReport(file);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
        byte[] fileBytes = new byte[0];

        if (format.equalsIgnoreCase("xls")){
            JRXlsExporter exporter = new JRXlsExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();
            fileBytes = outputStream.toByteArray();
        }

        if (format.equalsIgnoreCase("pdf")){
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

            SimplePdfReportConfiguration reportConfig
                    = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig
                    = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor(user.getUsername());
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint(PRINTING);

            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            exporter.exportReport();
            fileBytes = outputStream.toByteArray();
        }

        return fileBytes;
    }

    public void deleteById(long id) {
        this.jReportRepository.deleteById(id);
    }
}
