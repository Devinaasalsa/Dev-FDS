package hbm.fraudDetectionSystem.CommonEngine.CommServiceCaseManagement.enumeration;

public enum ClasificationType {

    HIGH("Clasify with High Priority"),//10

    MEDIUM("Clasify with Medium Priority"),//20

    LOW("Clasify with Low Priority");//30

    private final String clasificationType;

    ClasificationType(String clasificationType) {
        this.clasificationType = clasificationType;
    }

    public String getClasificationType() {
        return clasificationType;
    }
}
