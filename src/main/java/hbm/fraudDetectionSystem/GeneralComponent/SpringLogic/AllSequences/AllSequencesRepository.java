package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AllSequences;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AllSequences.AllSequencesQuery.UPDATE_SEQ_NUMBER_ORACLE_QUERY;
import static hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AllSequences.AllSequencesQuery.UPDATE_SEQ_NUMBER_QUERY;

@Repository
public interface AllSequencesRepository extends JpaRepository<AllSequences, Long> {
    @Query(value = UPDATE_SEQ_NUMBER_QUERY, nativeQuery = true)
    long updateSeqNumber(@Param("currValue") long currValue, @Param("dataId") Long dataId);

//    @Query(value = "BEGIN update_fds_seq(:currValue, :dataId, :updatedValue); END;", nativeQuery = true)
//    long updateSeqNumber(@Param("currValue") Long currValue, @Param("dataId") Long dataId, @Param("updatedValue") Long updatedValue);
}
