package hbm.fraudDetectionSystem.ApplicationParameters.SpringLogic.AppParameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationParametersServiceImpl implements ApplicationParametersService{

    @Autowired
    private ApplicationParametersRepository repository;

    @Override
    public List<ApplicationParameters> listAllApplicationParameters() {
        return repository.findAll();
    }

    @Override
    public ApplicationParameters addParameters(String paramName, String value, String description) {
        ApplicationParameters addParameters = new ApplicationParameters();
        addParameters.setParamName(paramName);
        addParameters.setValue(value);
        addParameters.setDescription(description);
        repository.save(addParameters);
        return addParameters;
    }

    @Override
    public ApplicationParameters updateParameters(Long currentId, String newParamName, String newValue, String newDescription) {
        ApplicationParameters updateParameters = repository.findApplicationParametersById(currentId);
        updateParameters.setParamName(newParamName);
        updateParameters.setValue(newValue);
        updateParameters.setDescription(newDescription);
        repository.save(updateParameters);
        return updateParameters;
    }

    @Override
    public void deleteParameters(long id) {
        ApplicationParameters parameters = repository.findApplicationParametersById(id);
        repository.deleteById(parameters.getId());
    }
}
