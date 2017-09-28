package nifi.worklow.param;

/**
 * @author qtran071917
 * @date Sep 1, 2017
 */

public interface Result {
	

	public static final int SUCCESS 											= 	0;
	
	public static final int FAILURE_CONFIGURATION_NOT_VALID						= 	1;
	
	public static final int FAILURE_NIFI_WORKFLOW_PROCESSOR_NAME_DUPLICATE		= 	2;
	
	public static final int FAILURE_NIFI_CONFIGURATION_PROCESSOR_NAME_DUPLICATE	= 	3;
	
	public static final int FAILURE_NIFI_PROCESSOR_NOT_MATCH					= 	4;
	
	public static final int FAILURE_NIFI_PROCESSOR_UPDATE_ERROR					= 	5;
	
}
