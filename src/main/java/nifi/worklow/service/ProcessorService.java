package nifi.worklow.service;

import nifi.worklow.param.Status;

import org.apache.nifi.web.api.dto.ProcessorDTO;
import org.apache.nifi.web.api.dto.status.ProcessorStatusDTO;
import org.apache.nifi.web.api.entity.ProcessorEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;


@Service
public class ProcessorService extends GenericService<ProcessorEntity> {
	
	
	public ProcessorService() {
		super(ProcessorEntity.class);
	}

	@Override
	public void init(String baseUrl) {
		super.init(baseUrl);
		this.serviceUrl = baseUrl.endsWith("/")? (baseUrl + "processors/") : (baseUrl +"/processors/");
	}
	
	
	/*
	 * Update processor 
	 */
	public void updateProcessor(ProcessorEntity processorEntity) {
		
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	    HttpEntity<ProcessorEntity> request = new HttpEntity<ProcessorEntity>(processorEntity, headers);
	    restTemplate.put(serviceUrl + processorEntity.getId(), request, ProcessorEntity.class);
	}
	
	/*
	 * Update processor status (start/stop)
	 */
	private void updateProcessorStatus(ProcessorEntity processorEntity, Status status) {
		
		ProcessorStatusDTO processorStatus = new ProcessorStatusDTO();
		processorStatus.setRunStatus(status.getValue());
		
		ProcessorDTO processorDTO =  new ProcessorDTO();
		processorDTO.setState(status.getValue().toUpperCase());
		processorDTO.setId(processorEntity.getId());
		
		ProcessorEntity updateProcessor = new ProcessorEntity();
		updateProcessor.setId(processorEntity.getId());
		updateProcessor.setRevision(processorEntity.getRevision());
		updateProcessor.setStatus(processorStatus);
		updateProcessor.setComponent(processorDTO);
		
		updateComponent(updateProcessor);
	}
	
	/*
	 * Start/stop a processor
	 */
	public void switchProcessorStatus(ProcessorEntity processorEntity,  Status status) {
		if (!isRunning(processorEntity) && status == Status.RUNNING) {
			updateProcessorStatus(processorEntity, Status.RUNNING);
		}
		if (isRunning(processorEntity) && status == Status.STOPPED) {
			updateProcessorStatus(processorEntity, Status.STOPPED);
		}
	}
	
	/*
	 * Check Processor is running
	 */
	private boolean isRunning(ProcessorEntity processorEntity) {
		if (processorEntity != null && processorEntity.getStatus().getAggregateSnapshot()!= null) {
			return processorEntity.getStatus().getAggregateSnapshot().getRunStatus().equalsIgnoreCase(Status.RUNNING.getValue());
		}
		return false;
	}

}
