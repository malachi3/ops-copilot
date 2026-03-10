package com.opscopilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OpsCopilot 运维智能体启动类
 * 
 * @version 1.0.0-MVP
 */
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════╗
            ║                                                           ║
            ║   🐱💙  OpsCopilot 运维智能体 - MVP 启动成功!            ║
            ║                                                           ║
            ║   API文档: http://localhost:8080/swagger-ui.html          ║
            ║                                                           ║
            ╚═══════════════════════════════════════════════════════════╝
            """);
    }
}
