package com.next.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.next.my.mapper.MyMapper;
import com.next.pojo.Staff;
import com.next.pojo.vo.StaffVO;

public interface StaffMapperCustom extends MyMapper<Staff> {
	
	/**
	 * 根据预告片的id和角色查询导演列表或者演员列表
	 * @return
	 */
	public List<StaffVO> queryStaffs(
            @Param(value = "trailerId") String trailerId,
            @Param(value = "role") Integer role);
	
}