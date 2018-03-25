package zyk.finance.dao.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import zyk.finance.domain.product.ProductEarningRate;

public interface ProductRateDAO extends JpaRepository<ProductEarningRate, Integer> {

	List<ProductEarningRate> findByProductId(int parseInt);

	@Modifying
	@Query("delete from ProductEarningRate per where per.productId=?1")
	void delByProId(int proId);

}
