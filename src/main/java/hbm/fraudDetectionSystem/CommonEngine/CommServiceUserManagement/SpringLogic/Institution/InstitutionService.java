package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Institution;



import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.InstitutionExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.InstitutionNotFoundException;

import java.util.List;
import java.util.Map;

public interface InstitutionService {
    Institution addInstitution(String institutionName, String description) throws InstitutionNotFoundException, InstitutionExistException;
    List<Institution> getInstitutions();
    Institution updateInstitution(String currentInstitutionName, String newInstitutionName, String newDescription) throws InstitutionNotFoundException, InstitutionExistException;
    void deleteInstitution(long id);
    Institution findInstitutionByInstitutionName(String institutionName);
    List<Institution> search(Map<String, Object> reqBody);
}
