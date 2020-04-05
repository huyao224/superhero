package com.next.service;

import java.util.List;

import com.next.pojo.vo.StaffVO;

public interface StaffService {

	/**
	 * 查询演职人员列表
	 * @param trailerId
	 * @param role
	 * @return
	 */
	public List<StaffVO> queryStaffs(String trailerId, Integer role);
	
}
