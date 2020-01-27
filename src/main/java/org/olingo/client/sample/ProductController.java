package org.olingo.client.sample;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private final OlingoSampleApp productService;

    @Autowired
    ProductController (OlingoSampleApp productService) {
        this.productService = productService;
    }
    @GetMapping(value = "/all")
    public List<Map<String,Object>> getAllProducts() throws IOException, ODataException {
        return productService.findAll();
    }

    
//    @PutMapping(value = "/place/{place}/salary/{percentage}")
//    public List<Employee> update(@PathVariable("place") String employeePlace ,@PathVariable("percentage") double percentInc) {
//    	employeeService.updateEmployee(employeePlace,percentInc);
//        return employeeService.findAll();
//    }
}