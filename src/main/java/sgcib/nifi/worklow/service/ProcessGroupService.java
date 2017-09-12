package sgcib.nifi.worklow.service;


import java.util.Set;

import org.apache.nifi.web.api.entity.ComponentEntity;
import org.apache.nifi.web.api.entity.ConnectionEntity;
import org.apache.nifi.web.api.entity.ConnectionsEntity;
import org.apache.nifi.web.api.entity.Entity;
import org.apache.nifi.web.api.entity.InputPortsEntity;
import org.apache.nifi.web.api.entity.OutputPortsEntity;
import org.apache.nifi.web.api.entity.PortEntity;
import org.apache.nifi.web.api.entity.ProcessGroupEntity;
import org.apache.nifi.web.api.entity.ProcessGroupsEntity;
import org.apache.nifi.web.api.entity.ProcessorEntity;
import org.apache.nifi.web.api.entity.ProcessorsEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author qtran071917
 */

@Service
public class ProcessGroupService extends GenericService<ProcessGroupEntity> {
	
	
	public ProcessGroupService() {
		super(ProcessGroupEntity.class);
	}
	
	@Override
	public void init(String baseUrl) {
		super.init(baseUrl);
		this.serviceUrl = baseUrl.endsWith("/")? (baseUrl + "process-groups/") : (baseUrl +"/process-groups/");
	}
	
	@Override
	public ProcessGroupEntity getById(String componentId) {
		String getProcessGroupUrl = serviceUrl + componentId;
		ResponseEntity<ProcessGroupEntity> processGroupResponseEntity = restTemplate.getForEntity(getProcessGroupUrl, ProcessGroupEntity.class);
		return processGroupResponseEntity.getBody();
	}
	
	public Set<ProcessGroupEntity> getProcessGroups() {
		String allProcessorGroups = serviceUrl + "root/process-groups";
		ResponseEntity<ProcessGroupsEntity> processGroupsEntity = restTemplate.getForEntity(allProcessorGroups, ProcessGroupsEntity.class);
		return processGroupsEntity.getBody().getProcessGroups();
	}
	
	
//	public Set<ProcessorEntity> getProcessorEntities(ProcessGroupEntity processGroupEntity) {
//		
//		String allProcessorsUrl = serviceUrl + processGroupEntity.getId() + "/processors";
//		ResponseEntity<ProcessorsEntity> processorsEntity = restTemplate.getForEntity(allProcessorsUrl, ProcessorsEntity.class);
//		return processorsEntity.getBody().getProcessors();
//		
//	}
	
	public Set<? extends ComponentEntity> getComponentsInGroup(ProcessGroupEntity processGroupEntity, Class<? extends ComponentEntity> componentType) {
		
		String componentInGroupsUrl = serviceUrl + processGroupEntity.getId();
		
		ResponseEntity<? extends Entity> componentEntities =  null;
		
		if (componentType == ProcessorEntity.class) {
			
			componentInGroupsUrl += "/processors";
			componentEntities = restTemplate.getForEntity(componentInGroupsUrl, ProcessorsEntity.class);

			ProcessorsEntity processorsEntity =	(ProcessorsEntity) componentEntities.getBody();
			return processorsEntity.getProcessors();
		}
		
		if (componentType == ConnectionEntity.class ) {
			componentInGroupsUrl += "/connections";
			componentEntities = restTemplate.getForEntity(componentInGroupsUrl, ConnectionsEntity.class);
			
			ConnectionsEntity connectionsEntity =	(ConnectionsEntity) componentEntities.getBody();
			return connectionsEntity.getConnections();
		}
		
		if (componentType == PortEntity.class ) {
			
			componentEntities = restTemplate.getForEntity(componentInGroupsUrl + "/input-ports", InputPortsEntity.class);
			InputPortsEntity inputPortsEntity =	(InputPortsEntity) componentEntities.getBody();
			
			Set<PortEntity> portsEntities =	inputPortsEntity.getInputPorts();
			
			componentEntities = restTemplate.getForEntity(componentInGroupsUrl + "/output-ports", OutputPortsEntity.class);
			OutputPortsEntity outputPortsEntity = (OutputPortsEntity) componentEntities.getBody();
			
			portsEntities.addAll(outputPortsEntity.getOutputPorts());
			
			return portsEntities;
		}
		
		return null;
	}
	

	public void updateProcessGroup(ProcessGroupEntity processGroupEntity) {
		
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<ProcessGroupEntity> request = new HttpEntity<ProcessGroupEntity>(processGroupEntity, headers);
	    restTemplate.put(serviceUrl + processGroupEntity.getId(), request, ProcessorEntity.class);
	}

}
