package restclient;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * 
 * @author pavithra
 * 
 */
public class RESTfulClient {

 public static final String BASE_URI = "http://localhost:8080/REST";
 public static final String PATH_BASIC_SEARCH = "/UserInfoService/basicSearch/";
 public static final String PATH_SPATIAL_SEARCH = "/UserInfoService/spatialSearch/";

 public static void main(String[] args) {
  
  String key = "Systems";
  String skip = "10";
  String count = "20";
  String lowerBound = "1980";
  String upperBound = "1988";
  
  
  int age = 25;

  ClientConfig config = new DefaultClientConfig();
  Client client = Client.create(config);
  WebResource resource = client.resource(BASE_URI);

  System.out.println("----------Basic Search----------");
  WebResource nameResource = resource.path("rest").path(PATH_BASIC_SEARCH + "key="+key+"/"+"skip="+skip+"/"+"count="+count);
  System.out.println("Client Response \n"
    + getClientResponse(nameResource));
  System.out.println("Response \n" + getResponse(nameResource) + "\n\n");

  
  System.out.println("----------Spatial Search----------");
  WebResource ageResource = resource.path("rest").path(PATH_SPATIAL_SEARCH + "key="+
  key+"/"+"lower="+lowerBound+"/"+"upper="+upperBound+"/"+"skip="+skip+"/"+"count="+count);
  System.out.println("Client Response \n"
    + getClientResponse(ageResource));
  System.out.println("Response \n" + getResponse(ageResource));
 }

 /**
  * Returns client response.
  * e.g : 
  * GET http://localhost:8080/RESTfulWS/rest/UserInfoService/name/Pavithra 
  * returned a response status of 200 OK
  *
  * @param service
  * @return
  */
 private static String getClientResponse(WebResource resource) {
  return resource.accept(MediaType.TEXT_XML).get(ClientResponse.class)
    .toString();
 }

 /**
  * Returns the response as XML
  * e.g : <User><Name>Pavithra</Name></User> 
  * 
  * @param service
  * @return
  */
 private static String getResponse(WebResource resource) {
  return resource.accept(MediaType.TEXT_XML).get(String.class);
 }
}