package com.next.service;

import com.next.pojo.Movie;
import com.next.utils.JqGridResult;

import java.util.List;

public interface MovieService {
    /**
     * 查询热门电影预告片
     *   评分越高 电影越热门
     *   点赞越多 预告越热门
     * **/
    public List<Movie> queryHotSuperhero(String type);

    /**
     * 查询电影数
     * **/
    public Integer queryAllTrailerCounts();

    /**
     *查询所有的电影记录
     * **/
    public List<Movie> queryAllSuperhero();

    /**
     *根据关键字查询分页
     * **/
    public JqGridResult searchTrailer(String keywords, Integer page, Integer pagesize);

    /**
     *查询电影详情
     * **/
    public Movie queryTrailerInfo(String trailerId);
}
