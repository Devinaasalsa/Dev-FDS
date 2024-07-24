package hbm.fraudDetectionSystem.NotificationEngine.enumeration;

public enum RecipientTypeEnum {
    RECIPIENT("RECIPIENT"),
    RECIPIENT_GROUP("RECIPIENT_GROUP");

    private final String recipientType;
    RecipientTypeEnum( String recipientType) {
        this.recipientType = recipientType;
    }

    public String getRecipientType() {
        return recipientType;
    }
}
