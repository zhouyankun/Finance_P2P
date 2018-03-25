package zyk.finance.service.product;

import java.util.List;

import zyk.finance.domain.product.Product;
import zyk.finance.domain.product.ProductEarningRate;

public interface ProductService {

	List<Product> findAll();
	public Product findById(long proId);
	List<ProductEarningRate> findRateByProId(String proId);
	void update(Product product);
}
