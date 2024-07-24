package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans;

import hbm.fraudDetectionSystem.GeneralComponent.Component.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CurrTransCacheService {
    protected final Cache<CurrTrans> cache = new Cache<>();
    protected final CurrTransRepository currTransRepository;

    @Autowired
    public CurrTransCacheService(CurrTransRepository cacheRepository) {
        this.currTransRepository = cacheRepository;
    }

    public synchronized void addData(CurrTrans data) {
        String utrnno = UUID.randomUUID().toString();
        cache.put(utrnno, data, 1);
    }

    @Scheduled(fixedDelay = 3000)
    public void saveDataToRepo() {
        List<CurrTrans> data = new ArrayList<>(cache.cacheMap.values()).stream().map(Cache.CacheObject::getValue).collect(Collectors.toList());
        currTransRepository.saveAll(data);
        data.forEach(t -> cache.remove(t.toString()));
    }

    @Scheduled(fixedDelay = 2000)
    public void removeExpiredCache() {
        long now = System.currentTimeMillis();
        cache.cacheMap.entrySet().removeIf(entry -> now > entry.getValue().getExpirationTime());
    }
}