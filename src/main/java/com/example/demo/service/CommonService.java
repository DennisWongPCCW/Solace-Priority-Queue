package com.example.demo.service;

import com.example.demo.controller.ProducerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
@PropertySource(value="classpath:application.yml")
@Service
public class CommonService {
//    @Autowired
//    private static ServerProperties serverProperties;
//    @Value("${server.address}")
//    private static String serverAddr;
//    @Value("${server.port}")
//    private static String serverPort;
//    @Value("${server.servlet.context-path}")
//    private static String contextPath;

    static Logger logger = LoggerFactory.getLogger(ProducerController.class);

    //webclient
    public static <T> void webClientPost(HttpServletRequest req,
                                         String endpoint,
                                         Object body,
                                         Class<T> responseType) {
//customise
//        WebClient client = WebClient.builder()
//                .baseUrl("http://localhost:8080")
//                .defaultCookie("cookieKey", "cookieValue")
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
//                .build();

        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromRequestUri(req).replacePath(req.getContextPath());
        String s = builder.build().toString();
        WebClient client = WebClient.create(s);
        logger.info("webClientPost - START",builder, s, req);

        client.post().uri(endpoint).bodyValue(body).retrieve()
                .bodyToMono(String.class).subscribe(re -> logger.info("webClientPost - async -", re));

    }
// rerun

}
