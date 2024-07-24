package hbm.fraudDetectionSystem.RuleEngine.Enum;

public enum HistoryAttributeEnum {

    CARD("hpan"),
    MERCHANTID("merchant_type"),
    TERMINALID("terminal_id"),
    CUSTOMERID("cif_id");
    private String name;
    HistoryAttributeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HistoryAttributeEnum fromName(String name) {
        for (HistoryAttributeEnum e : values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Name not found: " + name);
    }
}
