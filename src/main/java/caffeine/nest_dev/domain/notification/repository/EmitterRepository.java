package caffeine.nest_dev.domain.notification.repository;

import caffeine.nest_dev.domain.notification.entity.Notification;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {

    // 사용자별 SseEmitter 저장소
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // 사용자별로 전송된 Notification 이벤트 캐시 저장
    private final Map<String, Notification> eventCache = new ConcurrentHashMap<>();

    public SseEmitter save(String id, SseEmitter sseEmitter) {
        emitterMap.put(id, sseEmitter);
        return sseEmitter;
    }

    public void deleteById(String id) {
        emitterMap.remove(id);
    }

    public Map<String, Notification> findAllEventCacheByUserId(String userId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId + "_"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, SseEmitter> findAllEmitterByUserId(String userId) {
        return emitterMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId + "_"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void saveEventCache(String key, Notification notification) {
        eventCache.put(key, notification);
    }
}
