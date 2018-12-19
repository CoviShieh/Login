package service;

import entity.User;
import service.BaseService;

public interface UserService extends BaseService<User>  {

	/**
	 * 验证邮箱是否存在
	 * @param userEmail
	 * @return
	 */
	User validateEmailExist(String userEmail);
	
	/**
     * 验证用户是否存在,被激活了的邮箱才算是真正的用户
     * @param userEmail
     * @return
     */
	User validateUserExist(String userEmail);

	/**
	 * 对用户密码进行加密
	 * @param user
	 * @return
	 */
	User encryptedPassword(User user);
}
