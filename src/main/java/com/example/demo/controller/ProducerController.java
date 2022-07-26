package com.example.demo.controller;

import  com.example.demo.App;
import com.example.demo.service.RecordService;
import com.example.demo.service.SentService;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.entity.SubmitRequest;
//import com.solacesystems.common.util.Topic;
//import com.solacesystems.jcsmp.TextMessage;
//import com.solacesystems.jms.SolQueueSession;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController extends App {

    Logger logger = LoggerFactory.getLogger(ProducerController.class);
    @Autowired
    private RecordService recordService;
    @Autowired
    private SentService sentService;
//    @Autowired
//    private ReceiveService receiveService;
//    @Autowired
//    private JmsTemplate jmsTemplate;
//
//    public Connection connection;
//    public Session session;
//
//    @PostConstruct
//    private void customizeJmsTemplate() throws Exception {
//        System.out.println("customizeJmsTemplate");
////        Update the jmsTemplate's connection factory to cache the connection
////        CachingConnectionFactory ccf = new CachingConnectionFactory();
////        ccf.setTargetConnectionFactory(jmsTemplate.getConnectionFactory());
////        jmsTemplate.setConnectionFactory(ccf);
//
//        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
//        connectionFactory = (SolConnectionFactory) jmsTemplate.getConnectionFactory();
//        connection = connectionFactory.createConnection();
//        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//        // By default Spring Integration uses Queues, but if you set this to true you
//        // will send to a PubSub+ topic destination
////        jmsTemplate.setPubSubDomain(false);
//        logger.info("initializing connection to Solace:", connection);
//    }

//    @Value("SpringTestQueue")
//    private String queueName;
//
//    @Async
//    @Scheduled(fixedRate = 5000)
//    public void sendEvent() throws Exception {
//        String msg = "Hello World " + System.currentTimeMillis();
//        System.out.println("==========SENDING MESSAGE==========" + msg);
//
//        jmsTemplate.convertAndSend(queueName, msg);
//    }

    @RequestMapping("/")
    public String hello() {
        return "Hey, Hello World ! Hello World !";
    }

    @PostMapping("/sendQueue")
    public void sendToQueue(@RequestBody SubmitRequest sub, HttpServletRequest req, HttpServletResponse res) throws Exception {
        try {

            sentService.sendToQueue(sub, req, res);
        } catch (Exception e) {
            logger.error("Send - /sendQueue - Exception", e);
        }
    }

    @PostMapping("/sendTopic")
    public void sendToTopic(@RequestBody SubmitRequest sub, HttpServletRequest req, HttpServletResponse res) throws Exception {
        try {
            sentService.sendToTopic(sub, req, res);
        } catch (Exception e) {
            logger.error("Send - /sendTopic - Exception", e);
        }
    }
}