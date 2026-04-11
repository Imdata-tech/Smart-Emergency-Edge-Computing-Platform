package com.cardioguard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * CardioGuard 360 主应用类
 * Real-time Cardiovascular Health Monitoring Platform
 * 
 * @author CardioGuard Team
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.cardioguard.mapper")
@EnableAsync
@EnableScheduling
public class CardioGuardApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CardioGuardApplication.class, args);
        System.out.println("\n" +
                "╔═══════════════════════════════════════════════════╗\n" +
                "║                                                   ║\n" +
                "║     CardioGuard 360 - Backend Started            ║\n" +
                "║     Real-time Cardiovascular Health Monitor      ║\n" +
                "║                                                   ║\n" +
                "║     API Docs: http://localhost:8080/api/swagger  ║\n" +
                "║                                                   ║\n" +
                "╚═══════════════════════════════════════════════════╝");
    }
}
