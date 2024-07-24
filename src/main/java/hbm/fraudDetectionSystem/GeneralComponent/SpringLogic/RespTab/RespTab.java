package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.RespTab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "RESP_TAB")
public class RespTab {
    @Id
    @SequenceGenerator(name = "resp_tab_sequence", sequenceName = "resp_tab_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "resp_tab_sequence")
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String code;
    private String description;
}
