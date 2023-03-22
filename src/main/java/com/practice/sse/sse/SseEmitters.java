package com.practice.sse.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class SseEmitters {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /*
    SseEmitter를 생성할 때 비동기 요청이 완료되거나 타임아웃 발생 시 실행할 콜백을 등록할 수 있다.
    타임아웃이 발생할 경우 브라우저에서 재연결 요청을 보내는데 이 때 새로운 Emitter 객체를 다시 생성하기 때문에
    기존의 Emitter를 제거해주어야 한다.

    이 콜백은 SseEmitter를 관리하는 다른 스레드에서 실행된다. 따라서 thread-safe 한 자료구조를 사용하지않으면
    ConcurrentModificationException 이 발생할 수 있다. 그러므로 여기서는 ArrayList 가 아닌 CopyOnWriteArrayList 를 사용
     */
    SseEmitter add(SseEmitter emitter) {
        this.emitters.add(emitter);
        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(emitter); // 만료되면 리스트에서 삭제한다.
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });
        return emitter;
    }
}
