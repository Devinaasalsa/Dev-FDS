package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.AllSequences;

public class AllSequencesQuery {
    public static final String UPDATE_SEQ_NUMBER_QUERY_1 =
            "update all_sequences set curr_value = sub.sub_curr + sub.increment_by from (select curr_value as sub_curr, increment_by from all_sequences where id = :dataId) as sub  where id = :dataId returning curr_value";
    public static final String UPDATE_SEQ_NUMBER_QUERY =
            "update fds_all_sequences set curr_value = :currValue where id = :dataId returning curr_value";
    public static final String UPDATE_SEQ_NUMBER_ORACLE_QUERY =
            "update fds_all_sequences set currValue = :currValue where id = :dataId returning curr_value into :updatedValue";
}
