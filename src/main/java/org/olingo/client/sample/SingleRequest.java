package org.olingo.client.sample;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;

import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.springframework.stereotype.Service;
/**
 *
 */
@Service

public class SingleRequest {
  public static final String HTTP_METHOD_PUT = "PUT";
  public static final String HTTP_METHOD_POST = "POST";
  public static final String HTTP_METHOD_GET = "GET";

  public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
  public static final String HTTP_HEADER_ACCEPT = "Accept";

  public static final String APPLICATION_JSON = "application/json";
  public static final String APPLICATION_XML = "application/xml";
  public static final String APPLICATION_ATOM_XML = "application/atom+xml";
  public static final String APPLICATION_FORM = "application/x-www-form-urlencoded";
  public static final String METADATA = "$metadata";
  public static final String INDEX = "/index.jsp";
  public static final String SEPARATOR = "/";

  public static final boolean PRINT_RAW_CONTENT = true;
  private  String serviceUrl = "https://services.odata.org/Northwind/Northwind.svc";
  private String usedFormat = APPLICATION_ATOM_XML;
  private Edm edm;
  public SingleRequest() {}
  @PostConstruct
  // InputStream content = execute(absolutUri+"?$filter=UnitsInStock%20eq%2039", contentType, HTTP_METHOD_GET);
  public void init() throws Exception {
	  this.edm = readEdm(this.serviceUrl);
      ODataEntry entry =readEntry(this.edm, serviceUrl, usedFormat, "Products", "1",null);
    
  }

  public Edm readEdm(String serviceUrl) throws IOException, ODataException {
    InputStream content = execute(serviceUrl + SEPARATOR + METADATA, APPLICATION_XML, HTTP_METHOD_GET);
    return EntityProvider.readMetadata(content, false);
  }

  public ODataFeed readFeed(Edm edm, String serviceUri, String contentType, String entitySetName)
      throws IOException, ODataException {
    EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
    String absolutUri = createUri(serviceUri, entitySetName, null);
    InputStream content = execute(absolutUri, contentType, HTTP_METHOD_GET);
    return EntityProvider.readFeed(contentType,
        entityContainer.getEntitySet(entitySetName),
        content,
        EntityProviderReadProperties.init().build());
  }
  public List<Map<String,Object>> findAll() throws IOException, ODataException {
	  ODataFeed feed = readFeed(this.edm, serviceUrl, usedFormat, "Products");
	  List<ODataEntry> entryList = feed.getEntries();
	  List<Map<String, Object>> list=new ArrayList< Map<String, Object>>();
	  entryList.forEach((ODataEntry entry) -> {
		  if (entry != null) {
			  Map<String,Object> mp = entry.getProperties();
			  list.add(mp); 
		  }
      });
	  return list;
  }
  
  public ODataEntry readEntry(Edm edm, String serviceUri, String contentType, 
      String entitySetName, String keyValue, String expandRelationName)
      throws IOException, ODataException {
    // working with the default entity container
    EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
    // create absolute uri based on service uri, entity set name with its key property value and optional expanded relation name
    String absolutUri = createUri(serviceUri, entitySetName, keyValue, expandRelationName);

    InputStream content = execute(absolutUri, contentType, HTTP_METHOD_GET);

    return EntityProvider.readEntry(contentType,
        entityContainer.getEntitySet(entitySetName),
        content,
        EntityProviderReadProperties.init().build());
  }

  private InputStream logRawContent(String prefix, InputStream content, String postfix) throws IOException {
    if(PRINT_RAW_CONTENT) {
      byte[] buffer = streamToArray(content);
      return new ByteArrayInputStream(buffer);
    }
    return content;
  }

  private byte[] streamToArray(InputStream stream) throws IOException {
    byte[] result = new byte[0];
    byte[] tmp = new byte[8192];
    int readCount = stream.read(tmp);
    while(readCount >= 0) {
      byte[] innerTmp = new byte[result.length + readCount];
      System.arraycopy(result, 0, innerTmp, 0, result.length);
      System.arraycopy(tmp, 0, innerTmp, result.length, readCount);
      result = innerTmp;
      readCount = stream.read(tmp);
    }
    stream.close();
    return result;
  }

 

  
  private HttpStatusCodes checkStatus(HttpURLConnection connection) throws IOException {
    HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
    if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599) {
      throw new RuntimeException("Http Connection failed with status " + httpStatusCode.getStatusCode() + " " + httpStatusCode.toString());
    }
    return httpStatusCode;
  }

  private String createUri(String serviceUri, String entitySetName, String id) {
    return createUri(serviceUri, entitySetName, id, null);
  }

  private String createUri(String serviceUri, String entitySetName, String id, String expand) {
    final StringBuilder absolutUri = new StringBuilder(serviceUri).append(SEPARATOR).append(entitySetName);
    if(id != null) {
      absolutUri.append("(").append(id).append(")");
    }
    if(expand != null) {
      absolutUri.append("/?$expand=").append(expand);
    }
    return absolutUri.toString();
  }

  private InputStream execute(String relativeUri, String contentType, String httpMethod) throws IOException {
    HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

    connection.connect();
    checkStatus(connection);

    InputStream content = connection.getInputStream();
    content = logRawContent(httpMethod + " request on uri '" + relativeUri + "' with content:\n  ", content, "\n");
    return content;
  }


  private HttpURLConnection initializeConnection(String absolutUri, String contentType, String httpMethod)
      throws MalformedURLException, IOException {
    URL url = new URL(absolutUri);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod(httpMethod);
    connection.setRequestProperty(HTTP_HEADER_ACCEPT, contentType);
    if(HTTP_METHOD_POST.equals(httpMethod) || HTTP_METHOD_PUT.equals(httpMethod)) {
      connection.setDoOutput(true);
      connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
    }

    return connection;
  }
}
