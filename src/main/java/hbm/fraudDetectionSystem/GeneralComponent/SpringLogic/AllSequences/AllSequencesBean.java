package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AllSequences;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;
import static hbm.fraudDetectionSystem.GeneralComponent.Constant.LocalSequences.*;

@Configuration(proxyBeanMethods = false)
public class AllSequencesBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    private final AllSequencesService allSequencesService;

    @Autowired
    public AllSequencesBean(AllSequencesService allSequencesService) {
        this.allSequencesService = allSequencesService;
    }

    @Bean
    public AllSequences transactionIdSeq() {
        LOGGER.info("[FETCHING TRANSACTION ID SEQ]");

        try {
            AllSequences fixedData = allSequencesService.findBySeqNumber("1");
            if (fixedData.getId() != null) {
                if (fixedData.getCurrValue() < fixedData.getMinValue())
                    UTRNNO = fixedData.getMinValue();
                else
                    UTRNNO = fixedData.getCurrValue();

                INCREMENT_UTRNNO = fixedData.getIncrementBy();
                LOGGER.info(
                        String.format(
                                "Success fetch transaction id seq, current value: %s",
                                UTRNNO
                        )
                );
            } else {
                LOGGER.warn(
                        String.format(
                                "There is no sequence to be fetched."
                        )
                );
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH TRANSACTION ID SEQ]");
            return new AllSequences();
        } finally {
            LOGGER.info("[FETCH TRANSACTION ID SEQ FINISHED]");
        }
    }

    @Bean
    public AllSequences AuditIdSeq() {
        LOGGER.info("[FETCHING AUDIT ID SEQ]");

        try {
            AllSequences fixedData = allSequencesService.findBySeqNumber("2");
            if (fixedData.getId() != null) {
                if (fixedData.getCurrValue() < fixedData.getMinValue())
                    AUDIT_ID = fixedData.getMinValue();
                else
                    AUDIT_ID = fixedData.getCurrValue();

                INCREMENT_AUDIT_VAL = fixedData.getIncrementBy();
                LOGGER.info(
                        String.format(
                                "Success fetch audit id seq, current value: %s",
                                AUDIT_ID
                        )
                );

            } else {
                LOGGER.warn(
                        String.format(
                                "There is no sequence to be fetched."
                        )
                );
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH AUDIT ID SEQ]");
            return new AllSequences();
        } finally {
            LOGGER.info("[FETCH AUDIT ID SEQ FINISHED]");
        }
    }
}
