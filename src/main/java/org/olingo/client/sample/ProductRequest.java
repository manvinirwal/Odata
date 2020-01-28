package org.olingo.client.sample;

import java.io.Serializable;
import java.util.List;

public class ProductRequest implements Serializable {
	
	private List<Product> products;

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	
}
