package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SupportedMTI;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.SupportedMTI.SupportedMTIQuery.INSERT_MTI_QUERY;

@Repository
public interface SupportedMTIRepository extends JpaRepository<SupportedMTI, Long> {
    List<SupportedMTI> findAllByOrderByIdAsc();
    @Modifying
    @Query(value = INSERT_MTI_QUERY, nativeQuery = true)
    void saveData(@Param("value") String value, @Param("isResponse") boolean isResponse, @Param("isReversal") boolean isReversal);
}
