package com.gigigihuhuhu.websocket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigigihuhuhu.websocket.domain.message.ByeMsgDTO;
import com.gigigihuhuhu.websocket.domain.message.HelloMsgDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class SignalingController {
    private ConcurrentHashMap<String, String> clientSessions = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(SignalingController.class);
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/offer/{clientId}")
    public void offer(@DestinationVariable String clientId, @Payload String message) {
        logger.info("Received offer: {}", clientId, message);
        String sessionId = clientSessions.get(clientId);
        if (sessionId != null) {
            logger.info("Send offer: {}",sessionId, message);
            this.messagingTemplate.convertAndSendToUser(sessionId, "/queue/offer", message, createHeaders(sessionId));
        }
    }

    @MessageMapping("/answer/{clientId}")
    public void answer(@DestinationVariable String clientId, @Payload String message) {
        logger.info("Received answer: {}", clientId, message);
        String sessionId = clientSessions.get(clientId);
        if (sessionId != null) {
            logger.info("Send answer: {}",sessionId, message);
            this.messagingTemplate.convertAndSendToUser(sessionId, "/queue/answer", message, createHeaders(sessionId));
        }
    }

    @MessageMapping("/candidate/{clientId}")
    public void candidate(@DestinationVariable String clientId, @Payload String message) {
        logger.info("Received candidate: {}",clientId, message);
        String sessionId = clientSessions.get(clientId);
        if (sessionId != null) {
            logger.info("Send candidate: {}",sessionId, message);
            this.messagingTemplate.convertAndSendToUser(sessionId, "/queue/candidate", message, createHeaders(sessionId));
        }
    }

    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    public String hello(@Payload HelloMsgDTO helloMsg, SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {
        logger.info("Received hello: {}", helloMsg);
        String sessionId = headerAccessor.getSessionId();
        clientSessions.put(helloMsg.getClientId(), sessionId);
        return new ObjectMapper().writeValueAsString(helloMsg);
    }

    @MessageMapping("/bye")
    @SendTo("/topic/bye")
    public String bye(@Payload ByeMsgDTO byeMsg) throws JsonProcessingException {
        logger.info("Received bye: {}", byeMsg);
        clientSessions.remove(byeMsg.getClientId());
        return new ObjectMapper().writeValueAsString(byeMsg);
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}