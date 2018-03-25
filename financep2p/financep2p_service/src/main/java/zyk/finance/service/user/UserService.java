package zyk.finance.service.user;

import zyk.finance.domain.user.UserModel;

public interface UserService {

	UserModel findByUsername(String username);

	UserModel findByPhone(String phone);

	boolean addUser(UserModel user);

	UserModel login(String username, String password);

	UserModel findById(int userid);

}
