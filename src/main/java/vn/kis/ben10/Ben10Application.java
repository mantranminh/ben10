package vn.kis.ben10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;

@EnableFeignClients
@SpringBootApplication
public class Ben10Application {

    public static void main(String[] args) {
        SpringApplication.run(Ben10Application.class, args);
    }

    @Bean
    TomcatProtocolHandlerCustomizer<?> tomcatVirtualThreadProtocolHandlerCustomizer(){
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }
}
