package zyk.finance.service.product.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import zyk.finance.dao.product.ProductDAO;
import zyk.finance.dao.product.ProductRateDAO;
import zyk.finance.domain.product.Product;
import zyk.finance.domain.product.ProductEarningRate;
import zyk.finance.service.product.ProductService;
import zyk.finance.utils.ProductStyle;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDAO productDAO;
	@Autowired
	private ProductRateDAO productRateDAO;

	@Override
	public List<Product> findAll() {
		List<Product> products = productDAO.findAll();
		changeStatusToChinese(products);
		return products;
	}

	// 根据理财产品ID查询理财产品的利率信息
	@Override
	public List<ProductEarningRate> findRateByProId(String proId) {
		return productRateDAO.findByProductId(Integer.parseInt(proId));
	}

	@Override
	public Product findById(long proId) {
		// TODO Auto-generated method stub
		Product findOne = productDAO.findOne(proId);
		changeStatusToChinese(findOne);
		return findOne;
	}

	private void changeStatusToChinese(Product products) {
		List<Product> list = new ArrayList<>();
		list.add(products);
		changeStatusToChinese(list);
	}

	/**
	 * 方法描述：将状态转换为中文
	 * 
	 * @param products
	 *            void
	 */
	private void changeStatusToChinese(List<Product> products) {
		if (null == products)
			return;
		for (Product product : products) {
			int way = product.getWayToReturnMoney();
			// 每月部分回款
			if (ProductStyle.REPAYMENT_WAY_MONTH_PART.equals(String.valueOf(way))) {
				product.setWayToReturnMoneyDesc("每月部分回款");
				// 到期一次性回款
			} else if (ProductStyle.REPAYMENT_WAY_ONECE_DUE_DATE.equals(String.valueOf(way))) {
				product.setWayToReturnMoneyDesc("到期一次性回款");
			}

			// 是否复投 isReaptInvest 136：是、137：否
			// 可以复投
			if (ProductStyle.CAN_REPEAR == product.getIsRepeatInvest()) {
				product.setIsRepeatInvestDesc("是");
				// 不可复投
			} else if (ProductStyle.CAN_NOT_REPEAR == product.getIsRepeatInvest()) {
				product.setIsRepeatInvestDesc("否");
			}
			// 年利率
			if (ProductStyle.ANNUAL_RATE == product.getEarningType()) {
				product.setEarningTypeDesc("年利率");
				// 月利率 135
			} else if (ProductStyle.MONTHLY_RATE == product.getEarningType()) {
				product.setEarningTypeDesc("月利率");
			}

			if (ProductStyle.NORMAL == product.getStatus()) {
				product.setStatusDesc("正常");
			} else if (ProductStyle.STOP_USE == product.getStatus()) {
				product.setStatusDesc("停用");
			}

			// 是否可转让
			if (ProductStyle.CAN_NOT_TRNASATION == product.getIsAllowTransfer()) {
				product.setIsAllowTransferDesc("否");
			} else if (ProductStyle.CAN_TRNASATION == product.getIsAllowTransfer()) {
				product.setIsAllowTransferDesc("是");
			}
		}
	}

	@Override
	public void update(Product product) {
		// 修改利率信息
		List<ProductEarningRate> productId = productRateDAO.findByProductId((int) product.getProId());
		// 删除
		if (productId != null && productId.size() > 0) {
			productRateDAO.delByProId((int) product.getProId());
		}
		// 添加
		productRateDAO.save(product.getProEarningRate());
		// 修改产品信息
		productDAO.save(product);
	}

}
