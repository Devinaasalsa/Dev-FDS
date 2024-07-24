package hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CHANNEL_ENDPOINT")
public class ChannelEndpoint {
    @Id
    @SequenceGenerator(
            name = "channel_endpoint_sequence",
            sequenceName = "channel_endpoint_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "channel_endpoint_sequence"
    )
    private Long endpointId;

    private String url;

    @Type(type = "yes_no")
    private Boolean isAuth = false;

    private String sysMti;

    private Long configId;

    @Transient
    private String type; //Used for define the endpoint is type of auth,original, or reversal, currently for UI only

    @Transient
    private List<Map<String, Object>> states = new LinkedList<>(); //Used for hold field config by state (request/response), currently for UI only
}
