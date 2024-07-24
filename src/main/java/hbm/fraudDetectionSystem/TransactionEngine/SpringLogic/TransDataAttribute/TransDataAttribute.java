package hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.TransDataAttribute;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "trans_data_attr")
public class TransDataAttribute {
    @Id
    @SequenceGenerator(name = "trans_data_attr_sequence", sequenceName = "trans_data_attr_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "trans_data_attr_sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long attrId;
    @Column(name = "\"attribute\"")
    private String attribute;
    private String fieldTag;
    @Type(type = "yes_no")
    private Boolean addtData;

    @Column(name = "\"description\"")
    private String description;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name ="config_id",
            referencedColumnName = "configId"
    )
    private MessageConfiguration configId;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name ="endpoint_id",
            referencedColumnName = "endpointId"
    )
    private ChannelEndpoint endpoint;

    @Override
    public String toString() {
        return "TransDataAttribute{" +
                "attrId=" + attrId +
                ", attribute='" + attribute + '\'' +
                ", fieldTag='" + fieldTag + '\'' +
                ", addtData=" + addtData +
                ", description='" + description + '\'' +
                ", configId=" + configId +
                '}';
    }
}
