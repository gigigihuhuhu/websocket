package com.gigigihuhuhu.websocket.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SignalingController {

    private static final Logger logger = LoggerFactory.getLogger(SignalingController.class);

    @MessageMapping("/offer")
    @SendTo("/topic/offer")
    public String offer(@Payload String offer) {
        logger.info("Received offer: {}", offer);
        return offer;
    }

    @MessageMapping("/answer")
    @SendTo("/topic/answer")
    public String answer(@Payload String answer) {
        logger.info("Received answer: {}", answer);
        return answer;
    }

    @MessageMapping("/candidate")
    @SendTo("/topic/candidate")
    public String candidate(@Payload String candidate) {
        logger.info("Received candidate: {}", candidate);
        return candidate;
    }
}