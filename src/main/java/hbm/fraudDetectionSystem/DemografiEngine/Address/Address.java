package hbm.fraudDetectionSystem.DemografiEngine.Address;

import hbm.fraudDetectionSystem.DemografiEngine.Person.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_ADDRESS_INFO")
public class Address {
    @Id
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "address_seq")
    private Long addressId;
    private String addressType;
    private String country;
    private String addressName;
    private String region;
    private String city;
    private String street;
    private String house;
    private String apartment;
    private String postalCode;
    private String placeCode;
    private String regionCode;
    private float latitude;
    private float longitude;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "person_id", referencedColumnName = "personId")
    private Person person;
}
