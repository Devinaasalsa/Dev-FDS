package hbm.fraudDetectionSystem.DemografiEngine.Person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByPersonName(String personName);
}
