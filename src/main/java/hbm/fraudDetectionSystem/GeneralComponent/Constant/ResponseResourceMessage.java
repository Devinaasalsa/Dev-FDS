package hbm.fraudDetectionSystem.GeneralComponent.Constant;

public class ResponseResourceMessage {
    public static final String SUCCESS_FETCH_MESSAGE = "Success fetch data";
    public static final String DATA_NOT_FOUND_MESSAGE = "Data not found.";
    public static final String SUCCESS_ADD_MESSAGE = "Success add data";
    public static final String SUCCESS_UPDATE_MESSAGE = "Success update data";
    public static String SUCCESS_DELETE_MESSAGE(int totalData) {
        return String.format(
                "Success delete %d data",
                totalData
        );
    }
    public static String ISO_CONFIGURATION_MESSAGE_NOT_FOUND(String parentKey) {
        return String.format("ISO Configuration with id: %s not found.", parentKey);
    }
}
