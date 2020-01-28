package org.olingo.client.sample;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@XmlRootElement
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	private int productID;
	private String productName;
	private int supplierID;
	private int categoryID;
	private String  quantityPerUnit;
	private double unitPrice;
	private int unitsInStock;
	private int unitsOnOrder;
	private int reorderLevel;
	private boolean discontinued;
	

}
