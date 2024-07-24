package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTypeRepository extends JpaRepository<Type, Long> {
    Type findTypeByTypeName(String typeName);
}
