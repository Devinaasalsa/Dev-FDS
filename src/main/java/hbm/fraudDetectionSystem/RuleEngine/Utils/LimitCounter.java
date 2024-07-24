package hbm.fraudDetectionSystem.RuleEngine.Utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class LimitCounter {
    protected Map<String, CounterModel> counter = new LinkedHashMap<>();

    public Boolean containsKey(String key) {
        return this.counter.containsKey(key);
    }

    public void addKey(String key) {
        this.counter.put(key, new CounterModel());
    }

    public List<Map<String, String>> getCaptTrans(String key) {
        return this.counter.get(key).getDataModel();
    }

    public void removeCounter(String key) {
        this.counter.remove(key);
    }

//    @Scheduled(cron = "0 0 0 * * ?")
//    public void resetCounter() {
//        this.counter = new LinkedHashMap<>();
//    }

    @Getter
    @Setter
    class CounterModel {
        private int counter = 0;
        private List<Map<String, String>> dataModel = new LinkedList<>();
    }
}
