package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CurrTransRepository extends JpaRepository<CurrTrans, Long> {
//    @Query(value = "select * from curr_trans where utrnno=:utrnno", nativeQuery = true)
//    CurrTrans findByUtrnno(@Param("utrnno") Object utrnno);

    @Query(
            value = "SELECT c.*, count(trt.utrnno) as rule_trigger\n" +
                    "FROM curr_trans c\n" +
                    "left join t_rule_triggered trt ON c.utrnno = trt.utrnno\n" +
                    "group by c.sysdate, c.utrnno\n" +
                    "ORDER BY c.sysdate DESC",
            nativeQuery = true
    )
    List<CurrTrans> findAllOrderBySysdate();

    CurrTrans findByUtrnno(long utrnno);

//    @Query("SELECT c FROM CurrTrans c where c.hpan = :hpan ORDER BY FUNCTION('TO_TIMESTAMP', c.sysdate, 'YYYY-MM-DD HH24:MI:SS') DESC")
//    List<CurrTrans>findAllByHpanOrderBySysdate(String hpan);

    @Query("SELECT c FROM CurrTrans c where c.hpan = :hpan ORDER BY c.sysdate DESC")
    List<CurrTrans>findAllByHpanOrderBySysdate(String hpan);

    @Query(value = "SELECT * FROM Curr_Trans c where c.hpan = :hpan ORDER BY c.sysdate DESC", nativeQuery = true)
    List<Map<String, Object>> findTop500ByHpan(@Param("hpan") String hpan, Pageable pageable);

    //TODO:: Please lah MerchantData
//    @Query("SELECT c FROM CurrTrans c where c.merchant = :merchant ORDER BY c.sysdate DESC")
//    List<CurrTrans> findByMerchant(@Param("merchant") String merchant, Pageable pageable);

    @Query("SELECT c FROM CurrTrans c where c.terminalId = :terminalId ORDER BY c.sysdate DESC")
    List<CurrTrans> findByTerminalId(@Param("terminalId") String terminalId, Pageable pageable);

    //TODO:: Pleaselah Customer Data
 //   @Query("SELECT c FROM CurrTrans c where c.customer = :customer ORDER BY c.sysdate DESC")
   // List<CurrTrans> findByCustomer(@Param("customer") String customer, Pageable pageable);
}
