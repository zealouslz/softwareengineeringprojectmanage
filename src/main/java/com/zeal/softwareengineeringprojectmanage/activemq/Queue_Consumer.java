package com.zeal.softwareengineeringprojectmanage.activemq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;

//@Component
public class Queue_Consumer {
    @JmsListener(destination = "${myqueue}")
    public void receive(TextMessage textMessage) throws Exception{
        System.out.println("收到的消息"+textMessage.getText());
    }
}
