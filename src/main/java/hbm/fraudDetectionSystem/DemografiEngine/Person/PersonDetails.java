package hbm.fraudDetectionSystem.DemografiEngine.Person;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AttributeOverrides({
        @AttributeOverride(
                name = "surname",
                column = @Column(name = "person_name_surname")
        ),
        @AttributeOverride(
                name = "firstname",
                column = @Column(name = "person_name_firstname")
        ),
        @AttributeOverride(
                name = "secondname",
                column = @Column(name = "persone_name_secondname")
        )
})
public class PersonDetails {
    private String surname;
    private String firstName;
    private String secondName;
}
