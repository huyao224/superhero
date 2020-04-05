package com.next.api.controller;

import com.next.api.config.RabbitMQConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;

@RestController
@ApiIgnore
public class HelloController extends BasicController {

    @RequestMapping("/hello")
    public Object hello(){
        /**
         * routing key 路由规则
         * push.*.*   *代表一个占位符
         *    例： push.order.created
         * **/
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPICS_PUSH,
                "push.orders.created","订单创建了"+new Date());
        return "hello world";
    }


    @GetMapping("/redis/set")
    public Object redisSet(){
        redis.set("testRedisSet","hello-redis");
        return "hello world";
    }

    @GetMapping("/redis/get")
    public Object redisGet(){
        return redis.get("testRedisSet");
    }

}
