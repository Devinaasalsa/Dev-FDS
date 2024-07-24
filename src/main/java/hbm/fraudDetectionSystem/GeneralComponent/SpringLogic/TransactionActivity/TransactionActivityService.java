package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransactionActivity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TransactionActivityService {
    protected final TransactionActivityRepository transactionActivityRepository;

    @Autowired
    public TransactionActivityService(TransactionActivityRepository transactionActivityRepository) {
        this.transactionActivityRepository = transactionActivityRepository;
    }

    public List<TransactionActivity> fetchAllData() {
        return transactionActivityRepository.findAllByConvertedDateOrderByConvertedDateDesc(
                new SimpleDateFormat("yyyy-MM-dd").format(new Date())
        );

//        return transactionActivityRepository.findAllByConvertedDateOrderByConvertedDateDesc();
    }
}
