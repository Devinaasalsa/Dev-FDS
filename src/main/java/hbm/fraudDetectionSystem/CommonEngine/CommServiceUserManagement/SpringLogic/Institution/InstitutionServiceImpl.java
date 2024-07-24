package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Institution;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.InstitutionConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.InstitutionExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.InstitutionNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InstitutionServiceImpl implements InstitutionService {
    @PersistenceContext
    private EntityManager em;
    private final InstitutionRepository institutionRepository;

    public InstitutionServiceImpl(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    @Override
    public Institution addInstitution(String institutionName, String description) throws InstitutionNotFoundException, InstitutionExistException {
        validateNewInstitutionName(StringUtils.EMPTY, institutionName);
        Institution institution = new Institution();
        institution.setInstitutionName(institutionName);
        institution.setDescription(description);
        institutionRepository.save(institution);
        return institution;
    }

    @Override
    public List<Institution> getInstitutions() {
        return institutionRepository.findByOrderByIdAsc();
    }

    @Override
    public Institution updateInstitution(String currentInstitutionName, String newInstitutionName, String newDescription) throws InstitutionNotFoundException, InstitutionExistException {
        Institution currentInstitution = validateNewInstitutionName(currentInstitutionName, newInstitutionName);
        assert currentInstitution != null;
        currentInstitution.setInstitutionName(newInstitutionName);
        currentInstitution.setDescription(newDescription);
        institutionRepository.save(currentInstitution);
        return currentInstitution;
    }

    @Override
    public void deleteInstitution(long id) {
        institutionRepository.deleteById(id);
    }

    @Override
    public Institution findInstitutionByInstitutionName(String institutionName) {
        return institutionRepository.findInstitutionByInstitutionName(institutionName);
    }

    @Override
    public List<Institution> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Institution> query = cb.createQuery(Institution.class);
        Root<Institution> root = query.from(Institution.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (!value.toString().isEmpty()) {
                        predicates.add(cb.equal(root.get(key), value));
                    }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<Institution> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }

    private Institution validateNewInstitutionName(String currentInstitutionName, String newInstitutionName) throws InstitutionExistException, InstitutionNotFoundException {
        Institution institutionByNewInstitutionName = findInstitutionByInstitutionName(newInstitutionName);

        if (StringUtils.isNotBlank(currentInstitutionName)) {
            Institution currentInstitution = findInstitutionByInstitutionName(currentInstitutionName);
            if (currentInstitution == null) {
                throw new InstitutionNotFoundException(InstitutionConstant.NO_INSTITUTION_FOUND_BY_INSTITUTION_NAME + currentInstitutionName);
            }
            if (institutionByNewInstitutionName != null && !currentInstitution.getId().equals(institutionByNewInstitutionName.getId())) {
                throw new InstitutionExistException(InstitutionConstant.INSTITUTION_ALREADY_EXISTS);
            }

            return currentInstitution;
        } else {
            if (institutionByNewInstitutionName != null) {
                throw new InstitutionExistException(InstitutionConstant.INSTITUTION_ALREADY_EXISTS);
            }

            return null;
        }
    }
}
