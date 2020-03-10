package com.zeal.softwareengineeringprojectmanage;

import com.zeal.softwareengineeringprojectmanage.activemq.Queue_Produce;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import javax.jms.Queue;

@SpringBootTest(classes = SoftwareengineeringprojectmanageApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class activemqTest {
    @Autowired
    private Queue_Produce queue_produce;
   @Test
    public void testSend() throws Exception{
        queue_produce.produceMsgScheduled();
    }
}
