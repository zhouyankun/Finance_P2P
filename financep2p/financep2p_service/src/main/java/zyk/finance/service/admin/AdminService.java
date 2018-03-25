package zyk.finance.service.admin;

import zyk.finance.domain.admin.AdminModel;

public interface AdminService {
	public AdminModel login(String username, String password);
}
