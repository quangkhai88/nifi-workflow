package nifi.worklow.service;

import java.security.InvalidParameterException;

import org.apache.nifi.web.api.dto.search.SearchResultsDTO;
import org.apache.nifi.web.api.entity.ComponentEntity;
import org.apache.nifi.web.api.entity.ConnectionEntity;
import org.apache.nifi.web.api.entity.ProcessGroupEntity;
import org.apache.nifi.web.api.entity.ProcessorEntity;
import org.apache.nifi.web.api.entity.SearchResultsEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


/**
 * @author qtran071917
 */

public abstract class GenericService<T extends ComponentEntity> {
	
	private   Class<T> componentType;
	
	protected RestTemplate restTemplate = new RestTemplate();
	
	protected String baseUrl;
	
	protected String serviceUrl;
	
	public GenericService(Class<T> type) {
		this.componentType = type;
	}

	public T getComponentByName(String componentName) {
		
		String searchUrl = getBaseUrl() +  "/flow/search-results?q={componantName}";
		
		SearchResultsEntity searchResultEntity 	= 	restTemplate.getForObject(searchUrl, SearchResultsEntity.class, componentName);
		SearchResultsDTO searchResultsDTO 		=	searchResultEntity.getSearchResultsDTO();
		
		String componentId = null;
		
		if (componentType == ProcessGroupEntity.class && searchResultsDTO.getProcessGroupResults().size() > 0) {
			componentId = searchResultsDTO.getProcessGroupResults().get(0).getId();
		}
		
		if (componentType == ProcessorEntity.class && searchResultsDTO.getProcessorResults().size() > 0) {
			componentId = searchResultsDTO.getProcessorResults().get(0).getId();
		}
		
		if (componentType == ConnectionEntity.class && searchResultsDTO.getConnectionResults().size() >0) {
			componentId = searchResultsDTO.getConnectionResults().get(0).getId();
		}
		
		if (componentId == null) {
			throw new InvalidParameterException("Component not found by name : " + componentName);
		}
		
		return getById(componentId);
	}
	
	public String getServiceUrl() {
		return serviceUrl;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	protected void init(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public T getById(String componentId) {
		String getComponentUrl = serviceUrl + "{componentId}";
		ResponseEntity<T> componentEntity = restTemplate.getForEntity(getComponentUrl, componentType, componentId);
		return componentEntity.getBody();
	}

	protected void updateComponent(T component) {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	    HttpEntity<T> request = new HttpEntity<T>(component, headers);
	    restTemplate.put(serviceUrl + component.getId(), request, componentType);
	    
	}
}
