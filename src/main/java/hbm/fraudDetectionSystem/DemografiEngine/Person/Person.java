package hbm.fraudDetectionSystem.DemografiEngine.Person;

import hbm.fraudDetectionSystem.DemografiEngine.Customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_PERSON_INFO")
public class Person {
    @Id
    @SequenceGenerator(name = "person_seq", sequenceName = "person_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "person_seq")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long personId;
    private String personTitle;
    private String personName;
    private String suffix;
    private Timestamp birthday;
    private String placeOfBirth;
    private String gender;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @Embedded
    private PersonDetails personDetails;

}
