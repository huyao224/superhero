package com.next.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.next.mapper.MovieMapper;
import com.next.pojo.Movie;
import com.next.service.MovieService;
import com.next.utils.JqGridResult;
import com.next.utils.MovieTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieMapper movieMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Movie> queryHotSuperhero(String type) {
        //定义分页，永远只拿第一页的数据。分别是第一页的前8/前2数据
        Integer page = 1;
        Integer pageSize = 0;

        Example example = new Example(Movie.class);
        if (type.equals(MovieTypeEnum.SUPERHERO.type)){
            example.orderBy("score").desc();
            pageSize = 8;

        }else if(type.equals(MovieTypeEnum.TRAILER.type)){
            example.orderBy("prisedCounts").desc();
            pageSize = 4;
        }else {
            return null;
        }

        PageHelper.startPage(page,pageSize);

        return  movieMapper.selectByExample(example);
    }

    @Override
    public Integer queryAllTrailerCounts() {
        return movieMapper.selectCount(new Movie());
    }

    @Override
    public List<Movie> queryAllSuperhero() {
        return movieMapper.selectAll();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public JqGridResult searchTrailer(String keywords, Integer page, Integer pagesize) {
        Example example = new Example(Movie.class);
        example.orderBy("createTime").desc();
        Example.Criteria criteria =  example.createCriteria();
        criteria.orLike("name","%"+keywords+"%");
        criteria.orLike("originalName","%"+keywords+"%");

        PageHelper.startPage(page,pagesize);

        List<Movie> list = movieMapper.selectByExample(example);

        //获得分页后的分页数据
        PageInfo<Movie> pageList = new PageInfo<>(list);

        JqGridResult grid = new JqGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());

        return grid;
    }

    @Override
    public Movie queryTrailerInfo(String trailerId) {
        return movieMapper.selectByPrimaryKey(trailerId);
    }
}
