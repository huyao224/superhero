package com.next.api.config;

import com.next.pojo.bo.MovieMQMsgBO;
import com.next.utils.JsonUtils;
import com.next.utils.MoviePushUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class RabbitMQConsumer {
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_NAME_PUSH})
    public void listenMQPush(String payload, Message msg){
        MovieMQMsgBO movieMQMsgBO = JsonUtils.jsonToPojo(payload, MovieMQMsgBO.class);
        MoviePushUtil.dopush(movieMQMsgBO.getTitle(),movieMQMsgBO.getText());
    }
}
