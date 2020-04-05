package com.next.api.schedule;

import com.next.api.config.RabbitMQConfig;
import com.next.pojo.Movie;
import com.next.pojo.bo.MovieMQMsgBO;
import com.next.utils.DateUtil;
import com.next.utils.JsonUtils;
import com.next.utils.MoviePushUtil;
import com.next.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 定时任务处理类
 * **/
@Component
public class MyJob {
    @Autowired
    public RedisOperator redis;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //0 0 1 * * ?  每天1点执行
    //0/5 * * * * ? 每隔5秒执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void moviePush(){
        //获得第二天上映的电影
        Date tomorrow = DateUtil.dateIncreaseByDay(new Date(),1);
        String tomorrowStr = DateUtil.dateToString(tomorrow,DateUtil.ISO_EXPANDED_DATE_FORMAT);
        String releaseDatekey = "movie" + tomorrowStr;
        String  movieListStr = redis.get(releaseDatekey);
        if (StringUtils.isNotBlank(movieListStr)){
            List<Movie> movieList= JsonUtils.jsonToList(movieListStr, Movie.class);
            String movieNames = "";
            if(movieList != null && movieList.size()>0){
                for(Movie m : movieList){
                    movieNames += ("《"+ m.getName() + "》");
                }
            }
            String title = "新片速递！";
            String text = movieNames + "将于明日上映";


            //使用MQ进行业务解耦
//            MoviePushUtil.dopush(title,text);
            MovieMQMsgBO movieMQMsgBO = new MovieMQMsgBO(title,text);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPICS_PUSH,
                    "push.new.movie.display",JsonUtils.objectToJson(movieMQMsgBO));


        }
    }

}
