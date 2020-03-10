package com.zeal.softwareengineeringprojectmanage.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import java.util.UUID;

//@Component
public class Queue_Produce {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private Queue queue;
    public void produceMsg(){
        jmsMessagingTemplate.convertAndSend(queue,"****"+ UUID.randomUUID().toString().substring(0,6));
        System.out.println("***is over");
    }
    //间隔时间定投
    @Scheduled(fixedDelay = 3000)
    public void produceMsgScheduled(){
       jmsMessagingTemplate.convertAndSend(queue,"****"+ UUID.randomUUID().toString().substring(0,6));
        System.out.println("produceMsgScheduled send ok");
    }
}
