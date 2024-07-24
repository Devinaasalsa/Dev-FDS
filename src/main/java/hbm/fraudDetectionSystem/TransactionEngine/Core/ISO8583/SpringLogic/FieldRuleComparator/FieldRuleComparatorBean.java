package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldRuleComparator;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;
import static hbm.fraudDetectionSystem.GeneralComponent.Constant.CacheData.CACHE_RULE_COMPARATOR;

@Configuration(proxyBeanMethods = false)
public class FieldRuleComparatorBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    private final FieldRuleComparatorService fieldRuleComparatorService;

    @Autowired
    public FieldRuleComparatorBean(FieldRuleComparatorService fieldRuleComparatorService) {
        this.fieldRuleComparatorService = fieldRuleComparatorService;
    }
    
    @Bean
    public Map<String, FieldRuleComparator> ruleComparators() {
        LOGGER.info("[FETCHING FIELD RULE COMPARATOR]");

        try {
            //String = ComparatorId
            Map<String, FieldRuleComparator> fixedData = new LinkedHashMap<>();
            for (FieldRuleComparator fieldRuleComparator : fieldRuleComparatorService.findAllData()) {
                fixedData.put(fieldRuleComparator.getComparatorId().toString(), fieldRuleComparator);
            }

            if (fixedData.size() == 0) {
                CACHE_RULE_COMPARATOR = new LinkedHashMap<>();
                LOGGER.warn("[THERE IS NO DATA FOR FIELD RULE COMPARATOR]");
            } else {
                CACHE_RULE_COMPARATOR = fixedData;
                LOGGER.info(
                        String.format(
                                "Success fetch rule comparator with total: %s",
                                fixedData.size()
                        )
                );
            }

            return fixedData;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            LOGGER.error("[ERROR WHEN FETCH FIELD RULE COMPARATOR]");
            return new LinkedHashMap<>();
        } finally {
            LOGGER.info("[FETCH FIELD RULE COMPARATOR FINISHED]");
        }
    }
}
