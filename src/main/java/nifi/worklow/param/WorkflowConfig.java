package nifi.worklow.param;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author qtran071917
 * @date Aug 30, 2017
 */

@Component
@ConfigurationProperties(prefix="workflow")
public class WorkflowConfig {
	
	private String url;
	
	private List<ProcessorConfig> processors;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<ProcessorConfig> getProcessors() {
		return processors;
	}

	public void setProcessors(List<ProcessorConfig> processors) {
		this.processors = processors;
	}
	
	public static class ProcessorConfig {
		
		private String name;
		
		private String status;
		
		private String emptyQueues;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getEmptyQueues() {
			return emptyQueues;
		}

		public void setEmptyQueues(String emptyQueues) {
			this.emptyQueues = emptyQueues;
		}
	}
}

