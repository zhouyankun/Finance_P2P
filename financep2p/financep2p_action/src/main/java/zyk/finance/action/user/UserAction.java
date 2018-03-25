package zyk.finance.action.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.STEditAs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.object.UpdatableSqlQuery;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ModelDriven;

import zyk.finance.action.common.BaseAction;
import zyk.finance.action.filter.GetHttpResponseHeader;
import zyk.finance.cache.BaseCacheService;
import zyk.finance.domain.user.UserModel;
import zyk.finance.service.user.UserService;
import zyk.finance.service.userAcconut.UserAccountService;
import zyk.finance.utils.CommomUtil;
import zyk.finance.utils.ConfigurableConstants;
import zyk.finance.utils.FrontStatusConstants;
import zyk.finance.utils.ImageUtil;
import zyk.finance.utils.MD5Util;
import zyk.finance.utils.Response;
import zyk.finance.utils.TokenUtil;

@Namespace("/user")
@Controller
@Scope("prototype")
public class UserAction extends BaseAction implements ModelDriven<UserModel> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9119674545561949779L;
	private UserModel user = new UserModel();
	private Logger log = Logger.getLogger(UserAction.class);
	@Autowired
	private BaseCacheService baseCacheService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserAccountService userAccountService;

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	@Override
	public UserModel getModel() {
		return user;
	}

	// 获取用户安全等级
	@Action("userSecure")
	public void userSecure() {
		// 得到token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		// 从redis中获取token信息
		Map<String, Object> map = baseCacheService.getHmap(token);
		int userid = (int) map.get("id");
		// 根据用户id获取用户对象
		UserModel userModel = userService.findById(userid);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("phoneStatus", userModel.getPhoneStatus());
		data.put("realNameStatus", userModel.getRealNameStatus());
		data.put("payPwdStatus", userModel.getPayPwdStatus());
		data.put("emailStatus", userModel.getEmailStatus());
		list.add(data);
		try {
			// 回显数据
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
			return;
		} catch (Exception e) {
			log.info(e);
		}
	}

	// 退出logout
	@Action("logout")
	public void logout() {
		// 得到token
		String token = this.getRequest().getHeader("token");
		Map<String, Object> map = baseCacheService.getHmap(token);
		try {
			if (map == null || map.size() == 0) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
				return;
			}
			// 从redis删除
			baseCacheService.del(token);
			// 回显数据
			this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
			return;
		} catch (Exception e) {
			log.info(e);
		}
	}

	// 登录
	@Action("login")
	public void login() {
		// 得到请求参数
		String signUuid = this.getRequest().getParameter("signUuid");
		String signCode = this.getRequest().getParameter("signCode");
		// 验证请求参数
		try {

			if (StringUtils.isEmpty(signUuid)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
				return;
			} else if (StringUtils.isEmpty(signCode)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
				return;
			}

			String string = baseCacheService.get(signUuid);
			if (!string.equalsIgnoreCase(signCode)) {
				// 验证码不正确
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
				return;
			}
			// 调用service完成登录操作
			String str = user.getUsername();
			if (CommomUtil.isMobile(str)) {
				// 根据手机号查询
				UserModel uModel = userService.findByPhone(str);
				str = uModel.getUsername();
			}
			// 根据username查询 注意处理密码
			// String pwd = MD5Util.md5(str + user.getPassword().toLowerCase());
			String pwd = user.getPassword();
			UserModel u = userService.login(str, pwd);
			if (u != null) {
				// 登录成功
				// 把用户存储到redis
				String token = generateUserToken(u.getUsername());
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("username", u.getUsername());
				data.put("id", u.getId());
				// 响应数据到浏览器
				System.out.println(data);
				this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS)
						.setData(data).setToken(token).toJSON());
				return;
			} else {
				// 登录失败
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.ERROR_OF_USERNAME_PASSWORD).toJSON());
				return;
			}
			// 响应数据到浏览器
		} catch (Exception e) {
			log.info(e);
		}

	}

	// signup注册
	@Action("signup")
	public void regist() {
		// 封装请求数据
		// 调用userservice完成用户添加操作
		// String md5 = MD5Util.md5(user.getUsername() +
		// user.getPassword().toLowerCase());
		// user.setPassword(md5);
		boolean flag = userService.addUser(user);
		try {

			if (flag) {
				// 注册成功
				// 开户
				userAccountService.add(user.getId());
				// 把用户存储到redis中 有效时间为30分钟
				String token = generateUserToken(user.getUsername());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", user.getId());
				this.getResponse().getWriter().write(
						Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(map).setToken(token).toJSON());
				return;
			} else {
				// 注册失败
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.BREAK_DOWN).toJSON());
				return;
			}
		} catch (Exception e) {
			log.info(e);
		}
	}

	// 验证验证码
	@Action("codeValidate")
	public void codeValidate() {
		//
		String signUuid = this.getRequest().getParameter("signUuid");
		String signCode = this.getRequest().getParameter("signCode");
		String string = baseCacheService.get(signUuid);
		try {
			if (StringUtils.isEmpty(signCode)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
				return;
			}
			if (StringUtils.isEmpty(string)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
				return;
			}

			if (StringUtils.equalsIgnoreCase(signCode, string)) {
				this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
				return;
			} else {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
				return;
			}
		} catch (Exception e) {
			log.info(e);
		}

	}

	@Action("validatePhone")
	public void validatePhone() {
		// 得到上送的手机号
		String phone = this.getRequest().getParameter("phone");
		UserModel user = userService.findByPhone(phone);
		try {
			if (null == user) {
				this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
				return;
			} else {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.MOBILE_ALREADY_REGISTER).toJSON());
				return;
			}
		} catch (Exception e) {
			log.info(e);
		}

	}

	@Action("validateUserName")
	public void validateUserName() {
		// 得到上送的用户名
		String username = this.getRequest().getParameter("username");
		UserModel user = userService.findByUsername(username);
		try {
			if (null == user) {
				this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
				return;
			} else {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.ALREADY_EXIST_OF_USERNAME).toJSON());
				return;
			}
		} catch (Exception e) {
			log.info(e);
		}

	}

	// 得到图片验证码
	@Action("validateCode")
	public void validateCode() {
		String tokenUuid = this.getRequest().getParameter("tokenUuid");
		// 判断是否在redis中存在
		String uuid = baseCacheService.get(tokenUuid);

		try {
			if (StringUtils.isEmpty(uuid)) {
				this.getResponse().getWriter()
						.write(Response.build().setStatus(FrontStatusConstants.SYSTEM_ERROE).toJSON());
				return;
			}
			// 产生图片验证码
			String str = ImageUtil.getRundomStr();
			baseCacheService.del(uuid);
			baseCacheService.set(tokenUuid, str);
			baseCacheService.expire(tokenUuid, 3 * 60);
			ImageUtil.getImage(str, this.getResponse().getOutputStream());

		} catch (IOException e) {
			log.info(e);
		}

	}

	// 得到一个uuid
	@Action("uuid")
	public void uuid() {
		// 产生uuid
		String uuid = UUID.randomUUID().toString();
		// System.out.println("GG");
		// 存储到redis中
		baseCacheService.set(uuid, uuid);
		baseCacheService.expire(uuid, 3 * 60);
		// 响应到浏览器
		// System.out.println("GG");
		try {
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setUuid(uuid).toJSON());
			return;
		} catch (IOException e) {
			log.info(e);
		}
	}

	public String generateUserToken(String userName) {

		try {
			// 生成令牌
			String token = TokenUtil.generateUserToken(userName);

			// 根据用户名获取用户
			UserModel user = userService.findByUsername(userName);
			// 将用户信息存储到map中。
			Map<String, Object> tokenMap = new HashMap<String, Object>();
			tokenMap.put("id", user.getId());
			tokenMap.put("userName", user.getUsername());
			tokenMap.put("phone", user.getPhone());
			tokenMap.put("userType", user.getUserType());
			tokenMap.put("payPwdStatus", user.getPayPwdStatus());
			tokenMap.put("emailStatus", user.getEmailStatus());
			tokenMap.put("realName", user.getRealName());
			tokenMap.put("identity", user.getIdentity());
			tokenMap.put("realNameStatus", user.getRealNameStatus());
			tokenMap.put("payPhoneStatus", user.getPhoneStatus());

			baseCacheService.del(token);
			baseCacheService.setHmap(token, tokenMap); // 将信息存储到redis中

			// 获取配置文件中用户的生命周期，如果没有，默认是30分钟
			String tokenValid = ConfigurableConstants.getProperty("token.validity", "30");
			tokenValid = tokenValid.trim();
			baseCacheService.expire(token, Long.valueOf(tokenValid) * 60);

			return token;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("token", e);
			return Response.build().setStatus("-9999").toJSON();
		}
	}

}
