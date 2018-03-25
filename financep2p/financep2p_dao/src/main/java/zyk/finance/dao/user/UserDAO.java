package zyk.finance.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import zyk.finance.domain.user.UserModel;

public interface UserDAO extends JpaRepository<UserModel, Integer> {

	UserModel findByUsername(String username);

	UserModel findByPhone(String phone);

	@Query("select u from UserModel u where u.username=?1 and u.password=?2")
	UserModel login(String username, String password);

}
