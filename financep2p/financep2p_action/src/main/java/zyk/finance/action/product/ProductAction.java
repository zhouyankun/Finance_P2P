package zyk.finance.action.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ModelDriven;

import zyk.finance.action.common.BaseAction;
import zyk.finance.domain.product.Product;
import zyk.finance.domain.product.ProductEarningRate;
import zyk.finance.service.product.ProductService;
import zyk.finance.utils.FrontStatusConstants;
import zyk.finance.utils.JsonMapper;
import zyk.finance.utils.Response;

@Controller
@Scope("prototype")
@Namespace("/product")
public class ProductAction extends BaseAction implements ModelDriven<Product> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4118606667205161924L;

	private Product product = new Product();

	@Override
	public Product getModel() {
		return product;
	}

	private Logger log = Logger.getLogger(ProductAction.class);

	@Autowired
	private ProductService productService;

	// 修改操作
	@Action("modifyProduct")
	public void modifyProduct() {
		// 利用模型驱动得到请求参数，但是需要手动封装利率
		String proEarningRates = this.getRequest().getParameter("proEarningRates");
		// 将proEarningRates转换成list<proEarningRate>
		Map map = new JsonMapper().fromJson(proEarningRates, Map.class);

		List<ProductEarningRate> rates = new ArrayList<ProductEarningRate>();
		for (Object key : map.keySet()) {
			// key月份 value 利率值
			ProductEarningRate earningRate = new ProductEarningRate();
			earningRate.setMonth(Integer.parseInt(key.toString()));
			earningRate.setIncomeRate(Double.parseDouble(map.get(key).toString()));
			earningRate.setProductId((int) product.getProId());
			rates.add(earningRate);
		}
		product.setProEarningRate(rates);// 封装利率信息到产品
		// 调用service完成修改操作
		productService.update(product);
		try {
			this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
		} catch (IOException e) {
			log.info(e);
		}
	}

	// 根据ID查询利率
	@Action("findRates")
	public void findRates() {
		String proId = this.getRequest().getParameter("proId");
		List<ProductEarningRate> productEarningRates = productService.findRateByProId(proId);
		try {
			this.getResponse().getWriter().write(
					Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(productEarningRates).toJSON());
		} catch (IOException e) {
			log.info(e);
		}
	}

	// 根据ID查询
	@Action("findProductById")
	public void findById() {
		String proId = this.getRequest().getParameter("proId");
		this.getResponse().setCharacterEncoding("utf-8");
		Product findById = productService.findById(Long.parseLong(proId));
		try {
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(findById).toJSON());
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// 查找所有理财产品操作
	@Action("findAllProduct")
	public void findAll() {
		// 设置响应数据的编码
		this.getResponse().setCharacterEncoding("utf-8");
		// 调用service查询所有的数据
		List<Product> products = productService.findAll();
		// System.out.println("ggg");
		try {
			this.getResponse().getWriter()
					.write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(products).toJSON());
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
