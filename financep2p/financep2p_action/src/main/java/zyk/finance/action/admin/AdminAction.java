package zyk.finance.action.admin;

import java.io.IOException;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

import zyk.finance.action.common.BaseAction;
import zyk.finance.domain.admin.AdminModel;
import zyk.finance.service.admin.AdminService;

@Controller
@Namespace("/account")
@Scope("prototype")
@ParentPackage("json-default")
public class AdminAction extends BaseAction {
	@Autowired
	private AdminService adminService;

	@Action("login")
	public void login() {
		String username = this.getRequest().getParameter("username");
		String password = this.getRequest().getParameter("password");
		try {
			AdminModel admin = adminService.login(username, password);
			if (admin != null) {
				this.getResponse().getWriter().write("{\"status\":\"1\"}");
				return;
			} else {
				this.getResponse().getWriter().write("{\"status\":\"0\"}");
				return;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}