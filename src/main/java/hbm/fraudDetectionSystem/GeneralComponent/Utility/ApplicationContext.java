package hbm.fraudDetectionSystem.GeneralComponent.Utility;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContext implements ApplicationContextAware {

    private static org.springframework.context.ApplicationContext context;

    public static <T> T getBean(String beanName, Class<T> beanClass) {
        return context.getBean(beanName, beanClass);
    }

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext context) throws BeansException {
        setContext(context);
    }

    private static synchronized void setContext(org.springframework.context.ApplicationContext context) {
        ApplicationContext.context = context;
    }
}


//TODO: Sample for taking context from bean manually
//        currTransService = TransactionEngineContext.getBean("currTransService", CurrTransService.class);
//        isoHeaderConfigurationService = TransactionEngineContext.getBean("ISOHeaderConfigurationService", ISOHeaderConfigurationService.class);
//        isoFieldConfigurationService = TransactionEngineContext.getBean("ISOFieldConfigurationService", ISOFieldConfigurationService.class);