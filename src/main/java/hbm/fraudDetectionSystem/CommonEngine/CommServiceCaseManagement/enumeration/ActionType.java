package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.enumeration;

public enum ActionType {

    CLASIFY_ALERT("Clasify Alert"),

    OPEN_ALERT("Opening the Alert"),

    CLOSE_ALERT("Opening the Alert"),

    ADD_ALERT_COMMENT("Add Alert Comment"),//91

    FORWARDED_TO("Forward To"),//92

    ADD_CARD_TO_LIST("Add Card to List"), //93

    ADD_ACCOUNT_TO_LIST("Add Account to List"), //94

    ADD_MERCHANT_TO_LIST("Add merchant to List"), //95

    ADD_TERMINAL_TO_LIST("Add Terminal to List"), //96

    REMOVE_CARD_TO_LIST("Remove Card to List"), //70

    REMOVE_ACCOUNT_TO_LIST("Remove Account to List"),//71

    REMOVE_MERCHANT_TO_LIST("Remove merchant to List"), //72

    REMOVE_TERMINAL_TO_LIST("Remove Terminal to List"),  //73

    PUT_CARD_IN_WHITE_LIST("Put Card In Whitelist"), //82

    PUT_ACCOUNT_IN_WHITE_LIST("Put Account In Whitelist"), //83

    PUT_MERCHANT_IN_WHITE_LIST("Put Merchant In Whitelist"), //84

    PUT_TERMINAL_IN_WHITE_LIST("Put Terminal In Whitelist"), //85

    PUT_CARD_IN_BLACK_LIST("Put Card In Blacklist"), //86

    PUT_ACCOUNT_IN_BLACK_LIST("Put Account In Blacklist"), //87

    PUT_MERCHANT_IN_BLACK_LIST("Put Merchant In Blacklist"), //88

    PUT_TERMINAL_IN_BLACK_LIST("Put Terminal In Blacklist"); //89

    private final String actionType;

    public static ActionType findByNameActionType(String actionType){
        ActionType result = null;
        for (ActionType type : values()){
            if (type.name().equalsIgnoreCase(actionType)){
                result = type;
                break;
            }
        }
        return result;
    }

    ActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getName() {
        return actionType;
    }
}
