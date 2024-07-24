package hbm.fraudDetectionSystem.GeneralComponent.Exception;

import java.util.List;

public class DataNotFoundWhenDelete extends Exception {
    public DataNotFoundWhenDelete(List<Long> listId) {
        super(
                String.format(
                        "Data with id %s not found in backend, cancelling delete",
                        listId
                )
        );
    }
}
