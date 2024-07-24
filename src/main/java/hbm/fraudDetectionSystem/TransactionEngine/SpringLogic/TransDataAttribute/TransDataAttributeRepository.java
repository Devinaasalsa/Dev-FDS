package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransDataAttributeRepository extends JpaRepository<TransDataAttribute, Long> {
    @Query(
            nativeQuery = true,
            value = "select * from trans_data_attr where config_id is not null and addt_data = 'Y' order by \"attribute\" asc"
    )
    List<TransDataAttribute> findAllByAddtData();

    List<TransDataAttribute> findAllByConfigIdConfigIdOrderByAttribute(long configId);

    List<TransDataAttribute> findAllByEndpointEndpointIdOrderByAttribute(long endpointId);

    /*
        ini untuk select attribute trans dengan gabungan dari berbagai spec,
        jadi value yg valid hanya column attribute saja yang lainnya hanya dummy
     */
    @Query(
            nativeQuery = true,
            value = "SELECT\n" +
                    "    MAX(attr_id) AS attr_id,\n" +
                    "    MAX(addt_data) AS addt_data,\n" +
                    "    MAX(\"attribute\") AS attribute,\n" +
                    "    \"description\",\n" +
                    "    MAX(field_tag) AS field_tag,\n" +
                    "    MAX(config_id) AS config_id,\n" +
                    "    MAX(endpoint_id) AS endpoint_id\n" +
                    "FROM\n" +
                    "    trans_data_attr\n" +
                    "GROUP BY\n" +
                    "    \"description\"\n" +
                    "order by \"description\" asc"
    )
    List<TransDataAttribute> findByOrderByAttribute();

    @Query(
            nativeQuery = true,
            value = "select * from trans_data_attr where (config_id = :configId or config_id is null) and endpoint_id is null"
    )
    List<TransDataAttribute> findAllByConfigId(@Param("configId") long configId);

    @Query(
            nativeQuery = true,
            value = "select * from trans_data_attr where (config_id = :configId or config_id is null) and (ENDPOINT_ID = :endpointId or ENDPOINT_ID is null)"
    )
    List<TransDataAttribute> findAllByConfigIdAndEndpointId(@Param("configId")long configId, @Param("endpointId")long endpointId);

    Optional<TransDataAttribute> findByAttributeAndConfigIdConfigIdAndAddtData(String attr, Long configId, boolean addtData);

    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO trans_data_attr (attr_id, \"attribute\", field_tag, config_id, \"description\", addt_data, endpoint_id)\n" +
                    "SELECT\n" +
                    "    CASE\n" +
                    "        WHEN (SELECT max(attr_id) + 1 FROM trans_data_attr) IS NULL THEN 1\n" +
                    "        ELSE (SELECT max(attr_id) + 1 FROM trans_data_attr)\n" +
                    "    END AS attr_id,\n" +
                    "    :attribute,\n" +
                    "    :fieldTag,\n" +
                    "    :configId,\n" +
                    "    :description,\n" +
                    "    'Y', \n" +
                    "    :endpointId"
    )
    void saveData(
            @Param("attribute") String attribute,
            @Param("fieldTag") String fieldTag,
            @Param("configId") Long configId,
            @Param("description") String description,
            @Param("endpointId") Long endpointId
    );

    @Query(
            nativeQuery = true,
            value = "select * from trans_data_attr limit 1"
    )
    Optional<TransDataAttribute> findByAttribute(String attribute);
}
