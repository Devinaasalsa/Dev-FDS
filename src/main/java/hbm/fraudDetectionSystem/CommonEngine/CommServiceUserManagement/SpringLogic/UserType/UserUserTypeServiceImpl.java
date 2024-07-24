package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserType;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.UserTypeConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.TypeExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.TypeNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserUserTypeServiceImpl implements UserTypeService {
    private final UserTypeRepository userTypeRepository;

    public UserUserTypeServiceImpl(UserTypeRepository userTypeRepository) {
        this.userTypeRepository = userTypeRepository;
    }

    @Override
    public Type addType(String typeName) throws TypeNotFoundException, TypeExistException {
        validateNewType(StringUtils.EMPTY, typeName);
        Type type = new Type();
        type.setTypeName(typeName);
        userTypeRepository.save(type);
        return type;
    }

    @Override
    public List<Type> getTypes() {
        return userTypeRepository.findAll();
    }

    @Override
    public Type updateType(String currentTypeName, String newTypeName) throws TypeNotFoundException, TypeExistException {
        Type currentType = validateNewType(currentTypeName, newTypeName);
        assert currentType != null;
        currentType.setTypeName(newTypeName);
        userTypeRepository.save(currentType);
        return currentType;
    }

    @Override
    public void deleteType(long id) {
        userTypeRepository.deleteById(id);
    }

    @Override
    public Type findTypeByTypeName(String typeName) {
        return userTypeRepository.findTypeByTypeName(typeName);
    }

    private Type validateNewType(String currentTypeName, String newTypeName) throws TypeExistException, TypeNotFoundException {
        Type typeByNewTypeName = findTypeByTypeName(newTypeName);

        if (StringUtils.isNotBlank(currentTypeName)) {
            Type currentType = findTypeByTypeName(currentTypeName);
            if (currentType == null) {
                throw new TypeNotFoundException(UserTypeConstant.NO_TYPE_FOUND_BY_TYPE_NAME + currentTypeName);
            }
            if (typeByNewTypeName != null && !currentType.getId().equals(typeByNewTypeName.getId())) {
                throw new TypeExistException(UserTypeConstant.TYPE_ALREADY_EXISTS);
            }

            return currentType;
        } else {
            if (typeByNewTypeName != null) {
                throw new TypeExistException(UserTypeConstant.TYPE_ALREADY_EXISTS);
            }

            return null;
        }
    }
}
