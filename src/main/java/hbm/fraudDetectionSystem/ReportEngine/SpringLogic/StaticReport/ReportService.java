package hbm.fraudDetectionSystem.ReportEngine.SpringLogic.StaticReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;

@Service
public class ReportService {
    protected final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report generateReport(int reportType, Timestamp reportStartDate, Timestamp reportEndDate){
        switch (reportType) {
            case 1:
                return new Report().builder()
                        .reportId(reportType)
                        .reportDesc("Alert Statistic By Rule")
                        .fromDate(reportStartDate.toString())
                        .toDate(reportEndDate.toString())
                        .data(new ArrayList<>(reportRepository.fetchDataAlertStatisticByRule(reportStartDate, reportEndDate)))
                        .build();

            case 2:
                return new Report().builder()
                        .reportId(reportType)
                        .reportDesc("Alert Statistic Total")
                        .fromDate(reportStartDate.toString())
                        .toDate(reportEndDate.toString())
                        .data(new ArrayList<>(reportRepository.fetchDataAlertStatisticTotal(reportStartDate, reportEndDate)))
                        .build();

            case 3:
                return new Report().builder()
                        .reportId(reportType)
                        .reportDesc("User Activity History")
                        .fromDate(reportStartDate.toString())
                        .toDate(reportEndDate.toString())
                        .data(new ArrayList<>(reportRepository.fetchDataUserActivityHistory(reportStartDate, reportEndDate)))
                        .build();

            case 4:
                return new Report().builder()
                    .reportId(reportType)
                    .reportDesc("Transaction Decline Online")
                    .fromDate(reportStartDate.toString())
                    .toDate(reportEndDate.toString())
                    .data(new ArrayList<>(reportRepository.fetchDataDeclineOnline(reportStartDate, reportEndDate)))
                    .build();

            case 5:
                return new Report().builder()
                        .reportId(reportType)
                        .reportDesc("Card Had Transaction on Fraudulent Terminal")
                        .fromDate(reportStartDate.toString())
                        .toDate(reportEndDate.toString())
                        .data(new ArrayList<>(reportRepository.fetchDataCardHadTransOnFraudulentTerminal(reportStartDate, reportEndDate)))
                        .build();

            case 6:
                return new Report().builder()
                        .reportId(reportType)
                        .reportDesc("Rule Effectiveness")
                        .fromDate(reportStartDate.toString())
                        .toDate(reportEndDate.toString())
                        .data(new ArrayList<>(reportRepository.fetchDataRuleEffectiveness(reportStartDate, reportEndDate)))
                        .build();

            case 7:
                return new Report().builder()
                        .reportId(reportType)
                        .reportDesc("Card Added To Stop List")
                        .fromDate(reportStartDate.toString())
                        .toDate(reportEndDate.toString())
                        .data(new ArrayList<>(reportRepository.fetchDataCardAddedToStopList(reportStartDate, reportEndDate)))
                        .build();

            default:
                throw new RuntimeException("Report Type isn't listed");
        }
    }
}
