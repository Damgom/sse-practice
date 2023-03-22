package com.practice.sse.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@Slf4j
public class SseController {

    private final SseEmitters sseEmitters;

    public SseController(SseEmitters sseEmitters) {
        this.sseEmitters = sseEmitters;
    }

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect() {
        /*
        만료시간을 설정할 수 있다. default 값은 30초로 설정된다. 만료시간이 되면 브라우저에서 자동으로 서버에 재연결 요청
         */
        SseEmitter emitter = new SseEmitter(60 * 1000L);
        sseEmitters.add(emitter);
        /*
        emitter를 생성하고 나서 만료시간까지 아무런 데이터도 보내지 않으면 재연결 요청시 503 service unavailable 에러 발생할 수 있음
        그래서 최초 연결 시 더미 데이터를 전달해주는 것이 좋다.
         */
        try {
            emitter.send(SseEmitter.event()
                    .name("connect") // 해당 이벤트의 이름을 지정
                    .data("connected!")); // 더미데이터
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(emitter);
    }

    @PostMapping("/count")
    public ResponseEntity<Void> count() {
        sseEmitters.count();
        return ResponseEntity.ok().build();
    }
}
