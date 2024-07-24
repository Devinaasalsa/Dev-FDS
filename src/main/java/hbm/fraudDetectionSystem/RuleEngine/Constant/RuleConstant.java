package hbm.fraudDetectionSystem.RuleEngine.Constant;

public class RuleConstant {
    public static final String WAITING_CONFIRM_STAT = "Waiting Confirmation";
    public static final String CREATE_STAT = "Just Create";
    public static final String UPDATE_STAT = "Just Update";
    public static final String APPROVED_STAT = "Approved";
    public static final String REJECTED_STAT = "Rejected";

    public static String CREATE_HISTORY_MSG(String initiator) {
        return String.format(
                "%s created the rule",
                initiator
        );
    }
    public static String UPDATE_HISTORY_MSG(String initiator) {
        return String.format(
                "%s updated the rule",
                initiator
        );
    }
    public static String ACTIVATION_HISTORY_MSG(String initiator) {
        return String.format(
                "%s request to activate the rule",
                initiator
        );
    }
    public static String DEACTIVATION_HISTORY_MSG(String initiator) {
        return String.format(
                "%s request to deactivate the rule",
                initiator
        );
    }
    public static String APPROVE_CONFIRMATION_HISTORY_MSG(String initiator) {
        return String.format(
                "%s has approved the request",
                initiator
        );
    }
    public static String REJECTION_CONFIRMATION_HISTORY_MSG(String initiator) {
        return String.format(
                "%s has rejected the request",
                initiator
        );
    }
}
