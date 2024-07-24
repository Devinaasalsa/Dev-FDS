package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrAddtTransRepository extends JpaRepository<CurrAddtTrans, Long> {
    List<CurrAddtTrans> findAllByUtrnnoUtrnnoOrderByAttr(long utrnno);
}
