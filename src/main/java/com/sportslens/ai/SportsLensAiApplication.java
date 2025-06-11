package com.sportslens.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SportsLensAiApplication {

    public static void main(String[] args) {
        System.setProperty("java.net.useSystemProxies", "false");
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "80");
        System.setProperty("https.proxyHost", "");
        System.setProperty("https.proxyPort", "443");
        SpringApplication.run(SportsLensAiApplication.class, args);
    }
}