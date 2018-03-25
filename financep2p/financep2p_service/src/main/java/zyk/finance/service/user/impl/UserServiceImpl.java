package zyk.finance.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zyk.finance.dao.user.UserDAO;
import zyk.finance.domain.user.UserModel;
import zyk.finance.service.user.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDAO userDAO;

	@Override
	public UserModel findByUsername(String username) {
		return userDAO.findByUsername(username);
	}

	@Override
	public UserModel findByPhone(String phone) {
		return userDAO.findByPhone(phone);
	}

	@Override
	public boolean addUser(UserModel user) {
		UserModel save = userDAO.save(user);
		return save != null;
	}

	@Override
	public UserModel login(String username, String password) {
		return userDAO.login(username, password);
	}

	@Override
	public UserModel findById(int userid) {
		return userDAO.findOne(userid);
	}
}
