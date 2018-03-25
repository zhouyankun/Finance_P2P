package zyk.finance.service.userAcconut;

import zyk.finance.domain.userAccount.UserAccountModel;

public interface UserAccountService {

	void add(int id);

	UserAccountModel findById(int userId);

}
