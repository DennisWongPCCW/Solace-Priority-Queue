
package com.example.demo;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.controller.ProducerController;
//import com.solacesystems.common.util.Topic;
//import com.solacesystems.jcsmp.TextMessage;
//import com.solacesystems.jms.SolQueueSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
@PropertySource(value="classpath:application.yml")
@RestController
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
@EnableAsync
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.example.demo.DemoApplication.class, args);
    }

}
