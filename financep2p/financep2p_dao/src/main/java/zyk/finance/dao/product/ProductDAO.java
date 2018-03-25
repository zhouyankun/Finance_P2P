package zyk.finance.dao.product;

import org.springframework.data.jpa.repository.JpaRepository;

import zyk.finance.domain.product.Product;

public interface ProductDAO extends JpaRepository<Product, Long>{

}
