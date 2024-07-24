package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AllSequences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AllSequencesService {
    private final AllSequencesRepository allSequencesRepository;
    protected final JdbcTemplate jdbcTemplate;

    @Autowired
    public AllSequencesService(AllSequencesRepository allSequencesRepository, JdbcTemplate jdbcTemplate) {
        this.allSequencesRepository = allSequencesRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AllSequences> findAllData() {
        return allSequencesRepository.findAll();
    }

    public AllSequences findBySeqNumber(String sequenceNumber) {
        Optional<AllSequences> fetchedData = allSequencesRepository.findById(Long.valueOf(sequenceNumber));
        return fetchedData.orElseGet(AllSequences::new);
    }

    public long updateSeqNumber(long sequenceNumber, long seqId) {
        return allSequencesRepository.updateSeqNumber(sequenceNumber, seqId);
    }

//    public long updateSeqNumber(long sequenceNumber, long seqId) {
//        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
//                .withProcedureName("update_fds_seq")
//                .declareParameters(
//                        new SqlParameter("p_newValue", Types.NUMERIC),
//                        new SqlParameter("p_dataId", Types.NUMERIC),
//                        new SqlOutParameter("p_updatedValue", Types.NUMERIC)
//                );
//
//        Map<String, Object> result = jdbcCall.execute(sequenceNumber, seqId);
//        return ((BigDecimal) result.get("p_updatedValue")).longValue();
//    }
}
