package hbm.fraudDetectionSystem.ApplicationParameters.SpringLogic.AppParameters;

import java.util.List;

public interface ApplicationParametersService {
    List<ApplicationParameters>listAllApplicationParameters();

    ApplicationParameters addParameters(String paramName, String value, String description);

    ApplicationParameters updateParameters(Long currentId, String newParamName, String newValue, String newDescription);

    void deleteParameters(long id);
}
