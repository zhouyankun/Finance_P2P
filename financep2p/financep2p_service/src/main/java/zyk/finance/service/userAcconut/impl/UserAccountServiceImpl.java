package zyk.finance.service.userAcconut.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zyk.finance.dao.userAccount.UserAccountDAO;
import zyk.finance.domain.userAccount.UserAccountModel;
import zyk.finance.service.userAcconut.UserAccountService;

@Service
public class UserAccountServiceImpl implements UserAccountService {
	@Autowired
	private UserAccountDAO userAccountDAO;

	@Override
	public void add(int id) {
		UserAccountModel userAccountModel = new UserAccountModel();
		userAccountModel.setUserId(id);
		userAccountDAO.save(userAccountModel);
	}

	@Override
	public UserAccountModel findById(int userId) {
		return userAccountDAO.findByUserId(userId);
	}
}
