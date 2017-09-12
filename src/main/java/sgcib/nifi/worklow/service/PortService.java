package sgcib.nifi.worklow.service;

import org.apache.nifi.web.api.dto.PortDTO;
import org.apache.nifi.web.api.dto.status.PortStatusDTO;
import org.apache.nifi.web.api.entity.PortEntity;
import org.springframework.stereotype.Service;

import sgcib.nifi.worklow.param.Status;

/**
 * @author qtran071917
 */

@Service
public class PortService extends GenericService<PortEntity> {
	
	
	public PortService() {
		super(PortEntity.class);
	}

	@Override
	public void init(String baseUrl) {
		super.init(baseUrl);
	}
	
//	@Override
//	public PortEntity getById(String componentId) {
//		String getProcessorUrl = serviceUrl + "{componentId}";
//		ResponseEntity<PortEntity> responsePortEntity = restTemplate.getForEntity(getProcessorUrl, PortEntity.class, componentId);
//		return responsePortEntity.getBody();
//	}
	
//	public void updatePort(PortEntity portEntity) {
//		try {
//			
//		    HttpHeaders headers = new HttpHeaders();
//		    headers.setContentType(MediaType.APPLICATION_JSON);
//		    
//		    HttpEntity<PortEntity> request = new HttpEntity<PortEntity>(portEntity, headers);
//		    restTemplate.exchange(serviceUrl + portEntity.getId(), HttpMethod.PUT, request, PortEntity.class);
//		    
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//
//	}
	
	private void updatePortStatus(PortEntity portEntity, Status status) {
		
	    PortDTO portDto = new PortDTO();
	    portDto.setState(status.getValue().toUpperCase());
	    portDto.setId(portEntity.getId());
	    
	    PortStatusDTO newStatus  = new PortStatusDTO();
	    newStatus.setRunStatus(status.getValue());
	    
	    PortEntity updatePort = new PortEntity();
	    updatePort.setComponent(portDto);
	    updatePort.setId(portEntity.getId());
	    updatePort.setRevision(portEntity.getRevision());
	    updatePort.setStatus(newStatus);

	    updateComponent(updatePort);
	}
	
	public void switchPortStatus(PortEntity portEntity, Status status) {
		if (!isRunning(portEntity) && status == Status.RUNNING) {
			updatePortStatus(portEntity, Status.RUNNING);
		}
		if (isRunning(portEntity) && status == Status.STOPPED) {
			updatePortStatus(portEntity, Status.STOPPED);
		}
	}
	
	
	private boolean isRunning(PortEntity portEntity) {
		if (portEntity != null && portEntity.getStatus().getAggregateSnapshot()!= null) {
			return portEntity.getStatus().getAggregateSnapshot().getRunStatus().equalsIgnoreCase(Status.RUNNING.getValue());
		}
		return false;
	}

}
