package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.SpringLogic.FieldEncodingDesc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldEncodingDescRepository extends JpaRepository<FieldEncodingDesc, Long> {
    List<FieldEncodingDesc> findAllByOrderByEncodingIdAsc();
}
