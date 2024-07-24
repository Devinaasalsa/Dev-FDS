package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AllSequences;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "fds_all_sequences")
public class AllSequences {
    @Id
    @SequenceGenerator(name = "fds_all_seq_sequence", sequenceName = "fds_all_seq_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "fds_all_seq_sequence")
    private Long id;
    @Column(nullable = false, unique = true)
    private String sequenceName;
    @Column(nullable = false)
    private Long currValue;
    @Column(nullable = false)
    private Long minValue;
    @Column(nullable = false)
    private Long maxValue;
    @Column(nullable = false)
    private Long incrementBy;
}
