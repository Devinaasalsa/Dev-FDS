package hbm.fraudDetectionSystem.ChannelEngine.Domain;

import hbm.fraudDetectionSystem.ChannelEngine.Enum.EndpointType;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelEndpointHelper {
    private long endpointId;
    private String url;
    private long configId;
    private EndpointType endpointType;
}
