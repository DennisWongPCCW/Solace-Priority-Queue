package com.example.demo.service;

import com.example.demo.App;
import com.solacesystems.jcsmp.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Queue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.apache.tomcat.jni.SSLConf.finish;

public class ReplayService extends App {
    //TODO
    protected JCSMPSession session = null;
    XMLMessageProducer prod = null;
    @Autowired
    private JCSMPFactory solaceFactory;

    @Autowired(required=false) private JCSMPProperties jcsmpProperties;

    private ReplayFlowEventHandler consumerEventHandler = null;

    class ReplayFlowEventHandler implements FlowEventHandler {
        String flowName = null;
        ReplayFlowEventHandler(String name) {
            flowName = name;
        }
        @Override
        public void handleEvent(Object source, FlowEventArgs event) {
            System.out.println("Flow " + flowName + " (" + source + ") received flow event: " + event);
            if (event.getEvent() == FlowEvent.FLOW_DOWN) {
                if (event.getException() instanceof JCSMPErrorResponseException) {
                    JCSMPErrorResponseException ex = (JCSMPErrorResponseException) event.getException();
                    switch (ex.getSubcodeEx()) {
                        case JCSMPErrorResponseSubcodeEx.REPLAY_STARTED:
                        case JCSMPErrorResponseSubcodeEx.REPLAY_FAILED:
                        case JCSMPErrorResponseSubcodeEx.REPLAY_CANCELLED:
                        case JCSMPErrorResponseSubcodeEx.REPLAY_LOG_MODIFIED:
                        case JCSMPErrorResponseSubcodeEx.REPLAY_START_TIME_NOT_AVAILABLE:
                        case JCSMPErrorResponseSubcodeEx.REPLAY_MESSAGE_UNAVAILABLE:
                        case JCSMPErrorResponseSubcodeEx.REPLAYED_MESSAGE_REJECTED:
                        case JCSMPErrorResponseSubcodeEx.REPLAY_START_MESSAGE_UNAVAILABLE:
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    void checkCapability(final CapabilityType cap) {
        System.out.printf("Checking for capability %s...", cap);
        if (session.isCapable(cap)) {
            System.out.println("OK");
        } else {
            System.out.println("FAILED - exiting.");
            finish(1);
        }
    }

    public FlowReceiver replay (String dateStr, String queueName) throws JCSMPException, ParseException {
        consumerEventHandler = new ReplayFlowEventHandler("consumer");

        checkCapability(CapabilityType.MESSAGE_REPLAY);
        JCSMPSession session = JCSMPFactory.onlyInstance().createSession(jcsmpProperties);
        session.connect();
        ReplayStartLocation replayStart = null;
        FlowReceiver consumer = null;
        // Example dateStr parameter: String dateStr = "2019-04-05T13:37:00";
        if (dateStr != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Convert the given date into UTC time zone
            Date date = simpleDateFormat.parse(dateStr);
            replayStart = JCSMPFactory.onlyInstance().createReplayStartLocationDate(date);
        } else {
            replayStart = JCSMPFactory.onlyInstance().createReplayStartLocationBeginning();
        }

        ConsumerFlowProperties consumerFlowProps = new ConsumerFlowProperties();

        Queue queue = (Queue) JCSMPFactory.onlyInstance().createQueue(queueName);  // targeting this endpoint for replay
        consumerFlowProps.setEndpoint((Endpoint) queue);

        consumerFlowProps.setReplayStartLocation(replayStart);
        /*
         * Create and start a consumer flow
         */
        consumer = session.createFlow((XMLMessageListener) this, consumerFlowProps, null, consumerEventHandler);
        consumer.start();
        System.out.println("Flow (" + consumer + ") created");

        return consumer;
    }

}
