package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserType;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.TypeExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.TypeNotFoundException;

import java.util.List;

public interface UserTypeService {
    Type addType(String typeName) throws TypeNotFoundException, TypeExistException;
    List<Type> getTypes();
    Type updateType(String currentTypeName, String newTypeName) throws TypeNotFoundException, TypeExistException;
    void deleteType(long id);
    Type findTypeByTypeName(String typeName);
}
