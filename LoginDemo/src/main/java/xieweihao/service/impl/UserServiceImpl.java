package service.impl;

import java.util.UUID;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Service;

import entity.User;
import service.UserService;

@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

	@Override
	public User validateEmailExist(String userEmail) {
		return userMapper.validateEmailExist(userEmail);
	}

	@Override
	public User validateUserExist(String userEmail) {
		return userMapper.validateUserExist(userEmail);
	}

	/*
	 * 使用hash函数对密码加密
	 */
	@Override
	public User encryptedPassword(User user) {
		String salt = UUID.randomUUID().toString();
		Md5Hash md5Hash = new Md5Hash(user.getUserPassword(), salt, 2);
		user.setUserPassword(md5Hash.toString());
		user.setSalt(salt);
		return user;
	}

}
