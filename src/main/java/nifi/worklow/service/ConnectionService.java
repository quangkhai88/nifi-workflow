package nifi.worklow.service;

import org.apache.nifi.web.api.entity.ConnectionEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author qtran071917
 * @date Sep 7, 2017
 */

@Service
public class ConnectionService extends GenericService<ConnectionEntity> {

	public ConnectionService() {
		super(ConnectionEntity.class);
	}

	private String flowFileServiceUrl;
	
	@Override
	public void init(String baseUrl) {
		super.init(baseUrl);
		this.serviceUrl 		= baseUrl.endsWith("/")? (baseUrl + "connections/") : (baseUrl +"/connections/");
		this.flowFileServiceUrl = baseUrl + "flowfile-queues/";
	}
	
	public void emptyQueueConnection(ConnectionEntity connectionEntity) {
		
		String emptyflowFileConnection = flowFileServiceUrl + connectionEntity.getId() + "/drop-requests";
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	    String input = "{\"id\":\"" + connectionEntity.getId() + "\"}";
	    HttpEntity<String> entity = new HttpEntity<String>(input, headers);
	    
	    ResponseEntity<String> response = restTemplate.exchange(emptyflowFileConnection, HttpMethod.POST, entity, String.class);
	    
	}
	
	
	
}
