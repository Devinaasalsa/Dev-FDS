package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransTypeDesc;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper.LOGGER;

@Configuration(proxyBeanMethods = false)
public class TransTypeDescBean {
    private final Logger LOGGER = LOGGER(this.getClass().getName());
    protected final TransTypeDescService transTypeDescService;

    @Autowired
    public TransTypeDescBean(TransTypeDescService transTypeDescService) {
        this.transTypeDescService = transTypeDescService;
    }

    @Bean
    public List<TransTypeDesc> transTypes() {
        return transTypeDescService.fetchAllTransType();
    }
}
