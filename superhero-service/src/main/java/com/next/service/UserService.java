package com.next.service;

import com.next.pojo.Users;
import com.next.pojo.bo.MPWXUserBO;
import com.next.pojo.bo.RegistLoginUsersBO;

public interface UserService {

	/**
	 * 查询用户是否注册过
	 * @param openId
	 * @return
	 */
	public Users queryUserForLoginByMAWX(String openId);

	/**
	 * 用户注册
	 * @param openId
	 * @param mpwxUserBO
	 * @return
	 */
	public Users saveUserMAWX(String openId, MPWXUserBO mpwxUserBO);

	/**
	 * 查询用户名是否存在
	 * @param username
	 * @return
	 */
	public boolean queryUsernameIsExist(String  username);

	/**
	 * 用户登录
	 * @param username
	 * @param pwd
	 * @return
	 */
	public Users queryUserForLogin(String username,String pwd);

	/**
	 * 用户注册
	 * @param userBO
	 * @return
	 */
	public Users saveUser(RegistLoginUsersBO userBO);

	/**
	 * x修改用户信息
	 * @param user
	 * @return
	 */
	public Users updateUserInfo(Users user);
}
