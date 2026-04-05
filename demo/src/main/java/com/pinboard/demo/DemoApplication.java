package com.pinboard.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.pinboard.demo.pattern.observer.ActivityLogObserver;
import com.pinboard.demo.pattern.observer.PinNotificationService;
import com.pinboard.demo.pattern.singleton.ConfigurationManager;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        // Configurar o Singleton antes de iniciar a aplicação
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        configManager.setAppName("Pinboard");
        configManager.setVersion("1.0.0");
        configManager.setDebugMode(true);
        
        SpringApplication.run(DemoApplication.class, args);
    }
    
    @Bean
    public PinNotificationService pinNotificationService() {
        return new PinNotificationService();
    }

    @Bean
    public CommandLineRunner registerObservers(PinNotificationService notificationService,
                                                ActivityLogObserver activityLogObserver) {
        return args -> notificationService.registerObserver(activityLogObserver);
    }
}