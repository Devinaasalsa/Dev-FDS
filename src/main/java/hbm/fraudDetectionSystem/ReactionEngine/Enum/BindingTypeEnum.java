package hbm.fraudDetectionSystem.ReactionEngine.Enum;

public enum BindingTypeEnum {
    RULE("RULE"),
    RULE_GROUP("GROUP"),
    WHITE_LIST("WHITELIST"),
    BLACK_LIST("BLACKLIST"),;

    private String name;
    BindingTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static BindingTypeEnum fromName(String name) {
        for (BindingTypeEnum e : values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Name not found: " + name);
    }
}
