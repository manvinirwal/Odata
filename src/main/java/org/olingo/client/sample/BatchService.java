package org.olingo.client.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.annotation.PostConstruct;
import org.apache.olingo.odata2.api.batch.BatchException;
import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.batch.BatchRequestPart;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderBatchProperties;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.core.PathInfoImpl;
import org.apache.olingo.odata2.core.batch.BatchRequestWriter;
import org.apache.olingo.odata2.core.batch.v2.BatchParser;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.olingo.odata2.api.client.batch.BatchPart;
import org.apache.olingo.odata2.api.client.batch.BatchQueryPart;
import org.apache.olingo.odata2.api.client.batch.BatchSingleResponse;

@Service
public class BatchService {
	private static final String SERVICE_ROOT = "https://services.odata.org/Northwind/Northwind.svc";
	 private static final String BOUNDARY = "batch_8194-cf13-1f56";
	 private HttpClient m_httpClient = null;
	 private List<BatchPart> batch = new ArrayList<BatchPart>();
	 public BatchService() {}
	 
	 public List<Product> getProducts(List<Integer> productIds) throws BatchException, URISyntaxException, IOException {
			for (int i=0;i<productIds.size();i++) { 
				int id=productIds.get(i);
				addIndividualRequest(id);
			   }
			List<Product> products=new ArrayList<Product>();
			products=BatchRequest();
			return products;
		}
	  
	 private void addIndividualRequest(int id){
		    Map<String, String> headers = new HashMap<String, String>();
		    headers.put("Accept", "application/json");
		    BatchPart request = BatchQueryPart.method("GET").uri("Products("+id+")").headers(headers).build();
		    batch.add(request);
	 }
	 public List<Product> BatchRequest() throws URISyntaxException, BatchException, IOException {
 		List<Product>products=new ArrayList<Product>();
//		PathInfoImpl pathInfo = new PathInfoImpl();
//	    pathInfo.setServiceRoot(new URI(SERVICE_ROOT));
//	    parseProperties = EntityProviderBatchProperties.init().pathInfo(pathInfo).build();
	   
	    InputStream batchRequest = EntityProvider.writeBatchRequest(this.batch, BOUNDARY);
	    String payload = IOUtils.toString(batchRequest);
	    
	    HttpResponse batchResponse = executeBatchCall(SERVICE_ROOT, payload);
	    InputStream responseBody = batchResponse.getEntity().getContent();
		String contentType = batchResponse.getFirstHeader(
				HttpHeaders.CONTENT_TYPE).getValue();
		
		String response = IOUtils.toString(responseBody);
	 		List<BatchSingleResponse> responses = EntityProvider
	 				.parseBatchResponse(IOUtils.toInputStream(response),
	 						contentType);
	 		for (BatchSingleResponse rsp : responses) {
	 			if (Integer.parseInt(rsp.getStatusCode()) == 200 ) { 
	 				String result=rsp.getBody();
	 				ObjectMapper mapper = new ObjectMapper();
	 				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	 				mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	 		        Product product = mapper.readValue(result, Product.class);
	 		        products.add(product);
	 			}
	 		}
	 		return products;
	}
	private HttpResponse executeBatchCall(String serviceUrl, final String body) throws ClientProtocolException, IOException {
		final HttpPost post = new HttpPost(URI.create(serviceUrl + "/$batch"));
		post.setHeader("Content-Type", "multipart/mixed;boundary=" + BOUNDARY);
		HttpEntity entity = new StringEntity(body);

		post.setEntity(entity);

		HttpResponse response = getHttpClient().execute(post);


		return response;
	}
	private HttpClient getHttpClient() {
		if (this.m_httpClient == null) {
			this.m_httpClient = HttpClientBuilder.create().build();
		}
		return this.m_httpClient;
	}

	
	

}
