package com.next.api.config;

import com.next.pojo.Movie;
import com.next.service.MovieService;
import com.next.utils.DateUtil;
import com.next.utils.JsonUtils;
import com.next.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 自启动类
 * **/
@Component
public class AutoBootRunner implements ApplicationRunner {
    @Autowired
    private MovieService movieService;

    @Autowired
    private RedisOperator redis;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //1.获得所有的电影记录
        List<Movie> allList = movieService.queryAllSuperhero();

        //2.向Redis存入每一条电影的记录
        int movieIndex = 0;
        for(Movie movie : allList){
            movieIndex++;
            redis.set("guess-trailer-id:"+movieIndex, JsonUtils.objectToJson(movie));

            /**
             * 推送业务初始化
             * **/
            Date releaseDate = movie.getCreateTime();
            String releaseDateStr = DateUtil.dateToString(releaseDate,DateUtil.ISO_EXPANDED_DATE_FORMAT);
            String releaseDateKey = "movie" + releaseDateStr;
            String movieListStr = redis.get(releaseDateKey);
            List<Movie> releaseList = new ArrayList<Movie>();
            if(StringUtils.isNotBlank(movieListStr)){
                releaseList = JsonUtils.jsonToList(movieListStr,Movie.class);
            }
            releaseList.add(movie);
            redis.set(releaseDateKey,JsonUtils.objectToJson(releaseList));
        }
    }
}
