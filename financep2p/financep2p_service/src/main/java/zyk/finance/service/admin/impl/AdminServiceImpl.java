package zyk.finance.service.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zyk.finance.dao.domain.AdminDAO;
import zyk.finance.domain.admin.AdminModel;
import zyk.finance.service.admin.AdminService;
@Service
public class AdminServiceImpl implements AdminService {
	@Autowired
	private AdminDAO adminDAO;

	@Override
	public AdminModel login(String username, String password) {
		return adminDAO.login(username, password);
	}

}
