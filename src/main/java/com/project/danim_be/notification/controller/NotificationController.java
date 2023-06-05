//package com.project.danim_be.notification.controller;
//
//import com.project.danim_be.notification.service.NotificationService;
//import com.project.danim_be.security.auth.UserDetailsImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//@RestController
//@RequestMapping("/api/notification")
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private final NotificationService notificationService;
//
//    @GetMapping("/subscribe")
//    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        // 서비스를 통해 생성된 SseEmitter를 반환
//        return notificationService.connectNotification(userDetails.getMember().getId());
//    }
//}