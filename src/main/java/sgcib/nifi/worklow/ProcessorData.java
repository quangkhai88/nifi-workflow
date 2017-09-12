package sgcib.nifi.worklow;

import java.util.List;

import org.apache.nifi.web.api.entity.ConnectionEntity;
import org.apache.nifi.web.api.entity.PortEntity;
import org.apache.nifi.web.api.entity.ProcessorEntity;

/**
 * @author qtran071917
 * @date Sep 8, 2017
 */

public class ProcessorData {
	
	private ProcessorEntity processorEntity;
	
	private List<ConnectionEntity> connectionEntities;
	
	private List<PortEntity> inputPortEntities;
	
	private List<PortEntity> outputPortEntities;

	public List<PortEntity> getInputPortEntities() {
		return inputPortEntities;
	}

	public void setInputPortEntities(List<PortEntity> inputPortEntities) {
		this.inputPortEntities = inputPortEntities;
	}

	public List<PortEntity> getOutputPortEntities() {
		return outputPortEntities;
	}

	public void setOutputPortEntities(List<PortEntity> outputPortEntities) {
		this.outputPortEntities = outputPortEntities;
	}

	public ProcessorEntity getProcessorEntity() {
		return processorEntity;
	}

	public void setProcessorEntity(ProcessorEntity processorEntity) {
		this.processorEntity = processorEntity;
	}

	public List<ConnectionEntity> getConnectionEntities() {
		return connectionEntities;
	}

	public void setConnectionEntities(List<ConnectionEntity> connectionEntities) {
		this.connectionEntities = connectionEntities;
	}

}
