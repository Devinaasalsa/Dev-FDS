package hbm.fraudDetectionSystem.TransactionEngine.Core.ISO8583.Core.tag_mapper;

public interface TagMapper {
    String getTagForField(int subFldNo);
    Integer getFieldNumberForTag(String tag);
}
