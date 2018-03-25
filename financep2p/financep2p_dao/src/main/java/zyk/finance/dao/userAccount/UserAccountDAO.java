package zyk.finance.dao.userAccount;

import org.springframework.data.jpa.repository.JpaRepository;

import zyk.finance.domain.userAccount.UserAccountModel;

public interface UserAccountDAO extends JpaRepository<UserAccountModel, Integer> {
	public UserAccountModel findByUserId(int userId);
}
