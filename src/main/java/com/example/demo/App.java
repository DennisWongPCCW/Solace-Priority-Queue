package com.example.demo;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.Session;

public abstract class App {
    Logger logger = LoggerFactory.getLogger(App.class);
    @Autowired
    private JmsTemplate jmsTemplate;

    public Connection connection;
    public Session session;

    @PostConstruct
    private void customizeJmsTemplate() throws Exception {
        System.out.println("customizeJmsTemplate");
//        Update the jmsTemplate's connection factory to cache the connection
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
}
