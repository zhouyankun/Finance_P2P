package zyk.finance.action.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;

import zyk.finance.action.common.BaseAction;
import zyk.finance.action.filter.GetHttpResponseHeader;
import zyk.finance.cache.BaseCacheService;
import zyk.finance.domain.userAccount.UserAccountModel;
import zyk.finance.service.userAcconut.UserAccountService;
import zyk.finance.utils.FrontStatusConstants;
import zyk.finance.utils.Response;

@Namespace("/account")
@Controller
@Scope("prototype")
public class AccountAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 129059295802597781L;
	private Logger logger = Logger.getLogger(AccountAction.class);
	@Autowired
	private BaseCacheService baseCacheService;
	@Autowired
	private UserAccountService userAccountService;

	@Action("accountHomepage")
	public void accountHomepage() {
		// 得到token
		String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
		Map<String, Object> map = baseCacheService.getHmap(token);
		int userId = (int) map.get("id");
		UserAccountModel accountModel = userAccountService.findById(userId);
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("u_total", accountModel.getTotal());
		jsonObject.put("u_balance", accountModel.getBalance());
		jsonObject.put("u_interest_a", accountModel.getInterestA());
		list.add(jsonObject);

		try {
			// 回显数据
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
			return;
		} catch (Exception e) {
			logger.info(e);
		}
	}
}
