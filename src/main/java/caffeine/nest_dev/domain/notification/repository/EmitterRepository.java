package caffeine.nest_dev.domain.notification.repository;

import caffeine.nest_dev.domain.notification.entity.Notification;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {

    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    /**
     * 지정된 ID로 SseEmitter 인스턴스를 저장하고 반환합니다.
     *
     * @param id SseEmitter를 식별할 고유 ID
     * @param sseEmitter 저장할 SseEmitter 인스턴스
     * @return 저장된 SseEmitter 인스턴스
     */
    public SseEmitter save(String id, SseEmitter sseEmitter) {
        emitterMap.put(id, sseEmitter);
        return sseEmitter;
    }

    /**
     * 지정된 ID에 해당하는 SSE Emitter를 저장소에서 삭제합니다.
     *
     * @param id 삭제할 Emitter의 식별자
     */
    public void deleteById(String id) {
        emitterMap.remove(id);
    }

    /**
     * 지정한 사용자 ID로 시작하는 모든 이벤트 캐시 항목을 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return userId로 시작하는 키를 가진 이벤트 캐시의 맵
     */
    public Map<String, Object> findAllEventCacheStartWithId(String userId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId + "_"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 지정한 사용자 ID로 시작하는 모든 SseEmitter 엔트리를 반환합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return userId로 시작하는 키를 가진 SseEmitter 맵
     */
    public Map<String, SseEmitter> findAllStartWithId(String userId) {
        return emitterMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId + "_"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 지정된 키로 알림(Notification) 객체를 이벤트 캐시에 저장합니다.
     *
     * @param key 이벤트 캐시에 사용할 고유 키
     * @param notification 저장할 알림 객체
     */
    public void saveEventCache(String key, Notification notification) {
        eventCache.put(key, notification);
    }
}
