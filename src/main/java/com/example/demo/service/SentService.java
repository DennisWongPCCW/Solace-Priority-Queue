package com.example.demo.service;

import com.example.demo.entity.SentRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import javax.annotation.PostConstruct;
import javax.jms.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.entity.SubmitRequest;
//import com.solacesystems.common.util.Topic;
//import com.solacesystems.jcsmp.TextMessage;
//import com.solacesystems.jms.SolQueueSession;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.IOUtils;

@Service
public class SentService {

    Logger logger = LoggerFactory.getLogger(com.example.demo.controller.ProducerController.class);
    @Autowired
    private RecordService recordService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ResourceLoader resourceLoader;

    public Connection connection;
    public Session session;

    @SneakyThrows
    private SentRequest sentRequestBuilder(SubmitRequest sub, String classname){
        ObjectMapper mapper = new ObjectMapper();
        String string_ = mapper.writeValueAsString(sub);
        SentRequest toDB = SentRequest.builder()
                .id(recordService.generateID(Long.toString(System.currentTimeMillis())))
                .payload(string_)
                .status(null)
                .createdBy(classname)
                .remark(null).build();
    return toDB;
    }

    @PostConstruct
    private void customizeJmsTemplate() throws Exception {
        System.out.println("customizeJmsTemplate");
        // Update the jmsTemplate's connection factory to cache the connection
//        CachingConnectionFactory ccf = new CachingConnectionFactory();
//        ccf.setTargetConnectionFactory(jmsTemplate.getConnectionFactory());
//        jmsTemplate.setConnectionFactory(ccf);

        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory = (SolConnectionFactory) jmsTemplate.getConnectionFactory();
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // By default Spring Integration uses Queues, but if you set this to true you
        // will send to a PubSub+ topic destination
//        jmsTemplate.setPubSubDomain(false);
        logger.info("initializing connection to Solace:", connection);
    }

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


    public void sendToQueue(SubmitRequest sub, HttpServletRequest req, HttpServletResponse res) throws Exception {
        SentRequest toDB = sentRequestBuilder(sub, "sendToQueue");
        CommonService.webClientPost(req, "/internal/insertSentRequest",
                toDB,
                JsonNode.class);
        try {
            if (
                    sub.type.contains("Queue")
            ) {
                System.out.println("sendToQueue - SENDING MESSAGE : " + sub.path+ sub.payload );

                switch (sub.path) {
                    case "FileQueue":
                        jmsTemplate.convertAndSend(sub.path, sendBytesMessage(sub.payload));
                        break;
                    default:
                        jmsTemplate.convertAndSend(sub.path, sub.payload);
                }
                MessageProducer messageProducer = session.createProducer((Destination) topic);

                toDB.setStatus("Sent");
                CommonService.webClientPost(req, "/internal/updateSentRequest",
                        toDB,
                        JsonNode.class);
            }
        } catch (Exception e){
            System.out.println("==========Exception MSG=========="+e);
            logger.error("sendToQueue - Exception", e);

            toDB.setStatus("Error");
            CommonService.webClientPost(req, "/internal/updateSentRequest",
                    toDB,
                    JsonNode.class);
        }
    }

    public Message sendBytesMessage (Object payload) throws JMSException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        String filePath = "send/";

        System.out.println("Process file to message"+Paths.get(filePath));

        // get filename from payload
        Method m = payload.getClass().getMethod("filename");
        String filename = (String) m.invoke(payload);

        Resource resource = resourceLoader.getResource("classpath:"+filePath+filename);
        File file = resource.getFile();

        InputStream in = resource.getInputStream();
        // file to byte[], Path
//        byte[] bytes = Files.readAllBytes(Paths.get(filePath+filename));
        byte[] bytes = IOUtils.toByteArray(in);

        BytesMessage bytesMessage = null;
        bytesMessage.setStringProperty("filename",filename);
        bytesMessage.writeBytes(bytes);
        return bytesMessage;

    }

    public void sendToTopic(SubmitRequest sub, HttpServletRequest req, HttpServletResponse res) throws Exception{
        SentRequest toDB = sentRequestBuilder(sub, "sendToTopic");
        CommonService.webClientPost(req, "/internal/insertSentRequest",
                toDB,
                JsonNode.class);
        try {

            if (
                    sub.type.contains("Topic")
            ) {

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                Topic topic = (Topic) session.createTopic(sub.path);
                MessageProducer messageProducer = session.createProducer((Destination) topic);
                TextMessage message = (TextMessage) session.createTextMessage(sub.payload.toString());

                System.out.println("sendToTopic - SENDING MESSAGE : "+topic + message);

                messageProducer.send((Destination) topic, (Message) message, DeliveryMode.PERSISTENT,
                        Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
                // producer.setPriority(priorityLevel), or with producer.send(message, deliveryMode, priorityLevel)
                messageProducer.setPriority(9);
                toDB.setStatus("Sent");
                CommonService.webClientPost(req, "/internal/updateSentRequest",
                        toDB,
                        JsonNode.class);


            }
        } catch (Exception e)
        {
            System.out.println("==========Exception MSG=========="+e);
            logger.error("sendToTopic - Exception", e);

            toDB.setStatus("Error");
            CommonService.webClientPost(req, "/internal/updateSentRequest",
                    toDB,
                    JsonNode.class);
        }
    }

}
