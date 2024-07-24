package hbm.fraudDetectionSystem.ReactionEngine.Enum;

public enum ReactionEnum {
    CREATE_ALERT("CREATE_ALERT"),
    SET_RESPCODE("SET_RESPCODE"),
    ATTR_BLACK_LIST("ATTR_BLACK_LIST"),
    ATTR_WHITE_LIST("ATTR_WHITE_LIST"),
    ATTR_FRAUD_LIST("ATTR_FRAUD_LIST"),
    SMS_NOTIFICATION("SMS_NOTIFICATION"),
    EMAIL_NOTIFICATION("EMAIL_NOTIFICATION");

    private String name;
    ReactionEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ReactionEnum fromName(String name) {
        for (ReactionEnum e : values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Name not found: " + name);
    }
}
