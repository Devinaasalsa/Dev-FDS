package hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Core.Utils;

import hbm.fraudDetectionSystem.ChannelEngine.SpringLogic.ChannelEnpoint.ChannelEndpoint;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.Enum.JSONFieldState;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldAction.JSONFieldAction;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldConfiguration.JSONFieldConfiguration;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldFormatter.JSONFieldFormatter;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldType.JSONFieldType;
import hbm.fraudDetectionSystem.TransactionEngine.Core.JSON.SpringLogic.JSONFieldValue.JSONFieldValue;
import hbm.fraudDetectionSystem.TransactionEngine.SpringLogic.MessageConfiguration.MessageConfiguration;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@MappedSuperclass
@Getter
@Setter
public class JSONFieldHeaderDictionary {
    private String fieldName;
    private Integer length;
    private String padChar;
    private String letters; //Valid value are UC: UpperCase, LC: LowerCase
    private Integer sequence;
    private JSONFieldState state;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "data_type",
            referencedColumnName = "typeId"
    )
    private JSONFieldType dataType;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "formatter",
            referencedColumnName = "formatterId"
    )
    private JSONFieldFormatter formatter;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "config_id",
            referencedColumnName = "configId"
    )
    private MessageConfiguration msgConfig;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "endpoint_id",
            referencedColumnName = "endpointId"
    )
    private ChannelEndpoint endpoint;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("sequence ASC")
    @JoinColumn(name = "fieldId", referencedColumnName = "id")
    private Set<JSONFieldAction> actions = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "fieldId", referencedColumnName = "id")
    private Set<JSONFieldValue> validValues = new HashSet<>();

    @Transient
    private List<JSONFieldConfiguration> childField = new LinkedList<>();
}
