package org.olingo.client.sample;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.olingo.odata2.api.exception.ODataException;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private final SingleRequest productService;
    private final BatchService batchService;
    @Autowired
    ProductController (SingleRequest productService,BatchService batchService) {
        this.productService = productService;
        this.batchService=batchService;
    }
    @GetMapping(value = "/all")
    public List<Map<String,Object>> getAllProducts() throws IOException, ODataException {
        return productService.findAll();
    }
    
    @GetMapping(value = "/batch")
    public List<Product> products(@RequestBody ProductRequest body) throws IOException, ODataException, URISyntaxException {
    	List<Integer>productIds=new ArrayList<Integer>();
    	for(Product p :body.getProducts()) {
    		int id=p.getProductID();
    		productIds.add(id);
    	}
        return batchService.getProducts(productIds);
    }

    
//    @PutMapping(value = "/place/{place}/salary/{percentage}")
//    public List<Employee> update(@PathVariable("place") String employeePlace ,@PathVariable("percentage") double percentInc) {
//    	employeeService.updateEmployee(employeePlace,percentInc);
//        return employeeService.findAll();
//    }
}