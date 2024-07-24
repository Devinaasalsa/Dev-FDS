package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.TransactionStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionStatusService {
    protected final TransactionStatusRepository transactionStatusRepository;

    @Autowired
    public TransactionStatusService(TransactionStatusRepository transactionStatusRepository) {
        this.transactionStatusRepository = transactionStatusRepository;
    }

    public List<TransactionStatus> findAllByDateType(int dateType) {
        switch (dateType) {
            case 0:
                return this.transactionStatusRepository.fetchDataDaily();
            case 1:
                return this.transactionStatusRepository.fetchDataWeekly();
            case 2:
                return this.transactionStatusRepository.fetchDataMonthly();
        }

        return new ArrayList<>();
    }
}
