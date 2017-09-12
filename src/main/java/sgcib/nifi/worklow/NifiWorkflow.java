package sgcib.nifi.worklow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.web.api.entity.ConnectionEntity;
import org.apache.nifi.web.api.entity.PortEntity;
import org.apache.nifi.web.api.entity.ProcessGroupEntity;
import org.apache.nifi.web.api.entity.ProcessorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sgcib.nifi.worklow.param.Result;
import sgcib.nifi.worklow.param.Status;
import sgcib.nifi.worklow.param.WorkflowConfig;
import sgcib.nifi.worklow.param.WorkflowConfig.ProcessorConfig;
import sgcib.nifi.worklow.service.ConnectionService;
import sgcib.nifi.worklow.service.InputPortService;
import sgcib.nifi.worklow.service.OutPutPortService;
import sgcib.nifi.worklow.service.ProcessGroupService;
import sgcib.nifi.worklow.service.ProcessorService;

/**
 * @author qtran071917
 * @date Sep 8, 2017
 */


@Component
public class NifiWorkflow implements Result{
	
	@Autowired
	WorkflowConfig workflowConfiguration;
	
	@Autowired
	private ProcessorService processorService;
	
	@Autowired
	private ProcessGroupService processGroupService;
	
	@Autowired
	private InputPortService inputPortService;
	
	@Autowired
	private OutPutPortService outPutPortService;
	
	@Autowired
	private ConnectionService connectionService;
	
	
	private Map<String, ProcessorData> nifiProcessorsMap;
	
	private Map<String, ProcessorConfig> configProcessorsMap;
	
	private int updatedProcessorNumber = 0;
	
	private int totalProcessors = 0;
	
	public NifiWorkflow() {

		nifiProcessorsMap 	= new HashMap<String,ProcessorData>();
		
		configProcessorsMap = new HashMap<String,ProcessorConfig>();
	}
	
	public int initWorkflow() {
		
		ProcessGroupEntity rootNifiProcessGroup = null;
		String baseUrl = workflowConfiguration.getUrl();
		
		try {

			processorService.init(baseUrl);
			processGroupService.init(baseUrl);
			inputPortService.init(baseUrl);
			outPutPortService.init(baseUrl);
			connectionService.init(baseUrl);
			
			rootNifiProcessGroup = processGroupService.getById("root");
			
		} catch (Exception exception) {
			
			System.out.println("Can not reach Nifi Service with  the url " + baseUrl + " : " + exception);
			return FAILURE_CONFIGURATION_NOT_VALID;
		}
		
		int totalProcessor = rootNifiProcessGroup.getRunningCount() + rootNifiProcessGroup.getStoppedCount();
		setTotalProcessors(totalProcessor);
		
		if (totalProcessor != workflowConfiguration.getProcessors().size()) {
			
//			System.out.println("ERROR : The number of processors in configuration file and the number of processors in Nifi Service are not the same :");
//			System.out.println("\t There are " + totalProcessor + " processors work on Nifi but " + workflowConfiguration.getProcessors().size() + " processors in the Configuration file");
//			return FAILURE_CONFIGURATION_NOT_VALID;
		}
		

		for (ProcessorConfig processorConfig : workflowConfiguration.getProcessors()) {

			String processorName = processorConfig.getName();
			
			if (configProcessorsMap.get(processorName) != null) {
				System.out.println("ERROR : At least 2 processors in Configuration file have same name : " + processorName);
				return FAILURE_NIFI_CONFIGURATION_PROCESSOR_NAME_DUPLICATE;
			} else {
				configProcessorsMap.put(processorName, processorConfig);
			}
		}
		
		return SUCCESS;
	}

	
	/*
	 * Load  Nifi Processor with its connections and port (input port and output port)
	 */
	@SuppressWarnings("unchecked")
	public int loadAllNifiProcessors() {
		
		ProcessGroupEntity rootProcessGroup = processGroupService.getById("root");
		Set<ProcessGroupEntity> allProcessGroupEntities = processGroupService.getProcessGroups();
		allProcessGroupEntities.add(rootProcessGroup);
		
		for (ProcessGroupEntity processGroupEntity : allProcessGroupEntities) {
		
			Set<ProcessorEntity> 	processors 		= (Set<ProcessorEntity>) 	processGroupService.getComponentsInGroup(processGroupEntity, ProcessorEntity.class);
			Set<ConnectionEntity> 	connections 	= (Set<ConnectionEntity>) 	processGroupService.getComponentsInGroup(processGroupEntity, ConnectionEntity.class);
			Set<PortEntity> 		ports 			= (Set<PortEntity>) 		processGroupService.getComponentsInGroup(processGroupEntity, PortEntity.class);
			
			int result = loadProcessorsInGroup(processors, connections, ports);
			if (result != SUCCESS){
				return result;
			}
		}
		return SUCCESS;
	}
	
	
	/*
	 * In each group, load processors and their connections + port (input+output) 
	 */
	private int loadProcessorsInGroup(Set<ProcessorEntity> processorsSet, Set<ConnectionEntity> connectionsSet, Set<PortEntity> portsSet) {
		
		List<ProcessorEntity> 	processorListInGroup 	=  	new ArrayList<ProcessorEntity>();
		List<ConnectionEntity> 	connectionsListInGroup  = 	new ArrayList<ConnectionEntity>();
		Map<String, PortEntity> portMapInGroup 			= 	new HashMap<String,PortEntity>();
		
		processorListInGroup.addAll(processorsSet);
		connectionsListInGroup.addAll(connectionsSet);
		portsSet.forEach(portEntity -> portMapInGroup.put(portEntity.getId(), portEntity));
		
		for (ProcessorEntity processorEntity : processorListInGroup) {
			
			String processorName = processorEntity.getComponent().getName();
			
			if (nifiProcessorsMap.get(processorName) != null) {
				System.out.println("ERROR : At least 2 processors running in Nifi Workflow have same name : " + processorEntity.getComponent().getName());
//				return FAILURE_NIFI_WORKFLOW_PROCESSOR_NAME_DUPLICATE;
			}
			
			ProcessorData processorGroupData = new ProcessorData();
			
			processorGroupData.setProcessorEntity(processorEntity);
			processorGroupData.setConnectionEntities(new ArrayList<ConnectionEntity>());
			processorGroupData.setInputPortEntities(new ArrayList<PortEntity>());
			processorGroupData.setOutputPortEntities(new ArrayList<PortEntity>());
			
			for (ConnectionEntity connectionEntity : connectionsListInGroup) {
				if (connectionEntity.getDestinationId().equals(processorEntity.getId()) || connectionEntity.getSourceId().equals(processorEntity.getId())) {
					
					processorGroupData.getConnectionEntities().add(connectionEntity);
					
					String srcId = connectionEntity.getSourceId();
					String desId = connectionEntity.getDestinationId();
					
					PortEntity inputPort 	= portMapInGroup.get(srcId);
					PortEntity outputPort 	= portMapInGroup.get(desId);
					
					if (inputPort != null) {
						processorGroupData.getInputPortEntities().add(inputPort);
					}
					
					if (outputPort != null) {
						processorGroupData.getOutputPortEntities().add(outputPort);
					}
				}
			}
			
			nifiProcessorsMap.put(processorName, processorGroupData);
		}
		return SUCCESS;
	}
	
	
	
	 /* Verify Nifi workflow match the configuration, all processors in config file have to match all Nifi processors */
	public int validateNifiWorkFlowConfiguration() {
		
		for (Entry<String, ProcessorConfig> entry : configProcessorsMap.entrySet()) {
			
			String processorName = entry.getKey();
			
			if (nifiProcessorsMap.get(processorName) == null) {
				
				System.out.println("ERROR : processor " + processorName + " found in Configuration file but not found in Nifi");
				return FAILURE_NIFI_PROCESSOR_NOT_MATCH;
			}
		}

		return SUCCESS;
	}
	
	

	/*
	 * To start nifi workflow, update all processors by configuration
	 */
	public int startNifiWorkflowProcess() {
		
		for (Entry<String, ProcessorData> entry : nifiProcessorsMap.entrySet()) {
			
			ProcessorData 	processorData 	= entry.getValue();
			ProcessorConfig processorConfig = configProcessorsMap.get(entry.getKey());
			
			int result = updateNifiProcessor(processorData, processorConfig);
			
			if (result != SUCCESS) {
				return result;
			}
			
			updatedProcessorNumber ++;
		}
		
		return SUCCESS;
	}
	
	
	private int updateNifiProcessor(ProcessorData processorData, ProcessorConfig processorConfig) {
		
		try {
			
			Status status = getProcessorConfigStatus(processorConfig);
			Boolean emptyQueue = StringUtils.isNotEmpty(processorConfig.getEmptyQueues()) && Boolean.parseBoolean(processorConfig.getEmptyQueues());
			
			ProcessorEntity processorEntity = processorData.getProcessorEntity();
			
			/*  start/stop a processor and all input/output associated */
			processorService.switchProcessorStatus(processorEntity, status);
			
			for (PortEntity portEntity : processorData.getInputPortEntities()) {
				inputPortService.switchPortStatus(portEntity, status);
			}
			
			for (PortEntity portEntity : processorData.getOutputPortEntities()) {
				outPutPortService.switchPortStatus(portEntity, status);
			}

			/*  Empty all connections associated with processor */
			if (emptyQueue) {
				for (ConnectionEntity connectionEntity : processorData.getConnectionEntities()) {
					connectionService.emptyQueueConnection(connectionEntity);
				}
			}
		
		} catch (Exception e) {
			
			System.out.println(" Can not update processor " + processorData.getProcessorEntity().getComponent().getName() + " , got Error : " +  e);
			return FAILURE_NIFI_PROCESSOR_UPDATE_ERROR;
			
		}
		
		return SUCCESS;
	}
	
	
	
	private Status getProcessorConfigStatus(ProcessorConfig processorConfig) {
		if (StringUtils.isNotEmpty(processorConfig.getStatus()) && processorConfig.getStatus().equalsIgnoreCase("start")) {
			return Status.RUNNING;
		}
		return Status.STOPPED;
	}

	
	
	public int getUpdatedProcessorNumber() {
		return updatedProcessorNumber;
	}

	public void setUpdatedProcessorNumber(int updatedProcessorNumber) {
		this.updatedProcessorNumber = updatedProcessorNumber;
	}
	
	public int getTotalProcessors() {
		return totalProcessors;
	}

	public void setTotalProcessors(int totalProcessors) {
		this.totalProcessors = totalProcessors;
	}
	
}
